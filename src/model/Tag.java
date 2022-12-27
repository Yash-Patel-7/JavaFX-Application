package model;

import java.io.Serial;

/**
 * class for tags
 * @author Parth Patel, Yash Patel
 */
public final class Tag implements java.io.Serializable, Comparable<Tag> {
    /**
     * unique id for serialization
     */
    @Serial
    private static final long serialVersionUID = -6115814363901437624L;
    /**
     * tagType
     */
    public String type;
    /**
     * tagValue
     */
    public String value;

    /**
     * creates a tag
     * @param type tagType
     * @param value tagValue
     */
    public Tag(String type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * checks if two objects are equal
     * @param obj other object
     * @return if the two tags are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag other)) {
            return false;
        }
        return this.type.equals(other.type) && this.value.equals(other.value);
    }

    /**
     * compares two tags
     * @param other the object to be compared.
     * @return the order of the two tags
     */
    public int compareTo(Tag other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.type.compareToIgnoreCase(other.type) < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}