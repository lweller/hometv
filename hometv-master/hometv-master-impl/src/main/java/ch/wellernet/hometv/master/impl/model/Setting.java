/**
 *
 */
package ch.wellernet.hometv.master.impl.model;

import static java.lang.String.valueOf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ch.wellernet.hibernate.model.IdentifyableObject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Entity
@Table(schema = "HOMETV", name = "SETTING")
@SequenceGenerator(name = "primary_key", schema = "HOMETV", sequenceName = "SEQ_SETTING")
public class Setting extends IdentifyableObject<Integer> {

    private static final long serialVersionUID = 1L;

    public static final String INITIALIZED = "initialized";

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
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
