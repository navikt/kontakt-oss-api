package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon;

public class ClassificationCode {
    private String code;
    private String parentCode;
    private String level;
    private String name;
    private String presentationName;
    private String validFromInRequestedRange;
    private String validToInRequestedRange;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    public String getValidFromInRequestedRange() {
        return validFromInRequestedRange;
    }

    public void setValidFromInRequestedRange(String validFromInRequestedRange) {
        this.validFromInRequestedRange = validFromInRequestedRange;
    }

    public String getValidToInRequestedRange() {
        return validToInRequestedRange;
    }

    public void setValidToInRequestedRange(String validToInRequestedRange) {
        this.validToInRequestedRange = validToInRequestedRange;
    }
}
