/**
 *
 */
package model;

import static java.lang.String.valueOf;
import ch.wellernet.hometv.util.model.IdentifyableObject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class Setting extends IdentifyableObject<Integer> {

    private static final long serialVersionUID = 1L;

    public static final String INITIALIZED = "initialized";

    private String name;

    private String value;

    public Setting(String name) {
        this.name = name;
    }

    /**
     * Only used for internal purpose.
     */
    @SuppressWarnings("unused")
    private Setting() {
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Boolean getValueAsBoolean() {
        return new Boolean(value);
    }

    public Double getValueAsDouble() {
        return new Double(value);
    }

    public Float getValueAsFloat() {
        return new Float(value);
    }

    public Integer getValueAsInteger() {
        return new Integer(value);
    }

    public Long getValueAsLong() {
        return new Long(value);
    }

    public void setValue(Boolean value) {
        setValue(value == null ? null : valueOf(value));
    }

    public void setValue(Double value) {
        setValue(value == null ? null : valueOf(value));
    }

    public void setValue(Float value) {
        setValue(value == null ? null : valueOf(value));
    }

    public void setValue(Integer value) {
        setValue(value == null ? null : valueOf(value));
    }

    public void setValue(Long value) {
        setValue(value == null ? null : valueOf(value));
    }

    public void setValue(String value) {
        this.value = value;
    }
}
