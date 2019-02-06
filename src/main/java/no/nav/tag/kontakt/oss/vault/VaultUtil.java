package no.nav.tag.kontakt.oss.vault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import com.bettercloud.vault.response.LookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VaultUtil {
    private static final Logger logger = LoggerFactory.getLogger(VaultUtil.class);

    public static final String VAULT_TOKEN_PROPERTY = "VAULT_TOKEN";

    private static VaultUtil INSTANCE;
    private Vault vault;
    private Timer timer;

    private VaultUtil() {
        timer = new Timer("VaultScheduler", true);
    }

    // We should refresh tokens from Vault before they expire, so we add 30 seconds margin.
    // If the token is valid for less than 60 seconds, we use duration / 2 instead.
    public static long suggestedRefreshInterval(long duration) {
        if (duration < 60000) {
            return duration / 2;
        } else {
            return duration - 30000;
        }
    }

    public static VaultUtil getInstance() throws VaultError {
        if (INSTANCE == null) {
            VaultUtil util = new VaultUtil();
            util.init(); // might throw an exception
            INSTANCE = util;
        }
        return INSTANCE;
    }

    public Vault getClient() {
        return vault;
    }

    public Timer getTimer() {
        return timer;
    }

    private void init() throws VaultError {
        VaultConfig vaultConfig = null;
        try {
            vaultConfig = new VaultConfig()
                    .address(System.getenv().getOrDefault("VAULT_ADDR", "https://vault.adeo.no"))
                    .token(getVaultToken())
                    .openTimeout(5)
                    .readTimeout(30)
                    .sslConfig(new SslConfig().build())
                    .build();
        } catch (VaultException e) {
            throw new VaultError("Could not instantiate the Vault REST client", e);
        }

        vault = new Vault(vaultConfig);

        // Verify that the token is ok
        LookupResponse lookupSelf = null;
        try {
            lookupSelf = vault.auth().lookupSelf();
        } catch (VaultException e) {
            if (e.getHttpStatusCode() == 403) {
                throw new VaultError("The application's vault token seems to be invalid", e);
            } else {
                throw new VaultError("Could not validate the application's vault token", e);
            }
        }

        if (lookupSelf.isRenewable()) {
            final class RefreshTokenTask extends TimerTask {
                @Override
                public void run() {
                    try {
                        logger.info("Refreshing Vault token (old TTL = " + vault.auth().lookupSelf().getTTL() + " seconds)");
                        AuthResponse response = vault.auth().renewSelf();
                        logger.info("Refreshed Vault token (new TTL = " + vault.auth().lookupSelf().getTTL() + " seconds)");
                        timer.schedule(new RefreshTokenTask(), suggestedRefreshInterval(response.getAuthLeaseDuration() * 1000));
                    } catch (VaultException e) {
                        logger.error("Could not refresh the Vault token", e);
                    }
                }
            }
            logger.info("Starting a refresh timer on the vault token (TTL = " + lookupSelf.getTTL() + " seconds");
            timer.schedule(new RefreshTokenTask(), suggestedRefreshInterval(lookupSelf.getTTL() * 1000));
        }
        else {
            logger.warn("Vault token is not renewable");
        }
    }

    private static String getVaultToken() {
        try {
            Map<String, String> env = new HashMap<>(System.getenv());
            System.getProperties().forEach((key, value) -> {
                if(value instanceof String){
                    env.put((String) key, (String) value);
                }
            });
            if (env.containsKey(VAULT_TOKEN_PROPERTY) && !"".equals(env.get(VAULT_TOKEN_PROPERTY))) {
                return env.get(VAULT_TOKEN_PROPERTY);
            } else if (env.containsKey("VAULT_TOKEN_PATH")) {
                byte[] encoded = Files.readAllBytes(Paths.get(env.get("VAULT_TOKEN_PATH")));
                return new String(encoded, "UTF-8").trim();
            } else if (Files.exists(Paths.get("/var/run/secrets/nais.io/vault/vault_token"))) {
                byte[] encoded = Files.readAllBytes(Paths.get("/var/run/secrets/nais.io/vault/vault_token"));
                return new String(encoded, "UTF-8").trim();
            } else {
                throw new RuntimeException("Neither VAULT_TOKEN or VAULT_TOKEN_PATH is set");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not get a vault token for authentication", e);
        }
    }
}