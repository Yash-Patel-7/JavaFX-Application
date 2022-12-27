package model;

import javafx.util.Pair;

import java.io.File;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * class for photos
 * @author Parth Patel, Yash Patel
 */
public final class Photo implements java.io.Serializable, Comparable<Photo> {
    /**
     * unique id for serialization
     */
    @Serial
    private static final long serialVersionUID = -1207060131086150206L;
    /**
     * location of photo on user's local disk
     */
    public String path;
    /**
     * caption for photo
     */
    public String caption;
    /**
     * creation date for photo
     */
    public Calendar dateTaken;
    /**
     * list of tags
     */
    public ArrayList<Tag> tags;

    /**
     * creates a photo object
     * @param path location of photo locally
     */
    public Photo(String path) {
        this.path = path;
        this.caption = "";
        File image = new File(path);
        this.dateTaken = Calendar.getInstance();
        this.dateTaken.setTimeInMillis(image.lastModified());
        this.dateTaken.set(Calendar.MILLISECOND, 0);
        this.tags = new ArrayList<>();
    }

    /**
     * creates a photo object with a caption
     * @param path the photo's path
     * @param caption the photo's caption
     */
    public Photo(String path, String caption) {
        this.path = path;
        this.caption = caption;
        File image = new File(path);
        this.dateTaken = Calendar.getInstance();
        this.dateTaken.setTimeInMillis(image.lastModified());
        this.dateTaken.set(Calendar.MILLISECOND, 0);
        this.tags = new ArrayList<>();
    }

    /**
     * checks if two objects are equal
     * @param obj the other object
     * @return if photos are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Photo other)) {
            return false;
        }
        return this.path.equals(other.path);
    }

    /**
     * compares one photo with another
     * @param other the object to be compared.
     * @return value to represent order of photos
     */
    public int compareTo(Photo other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.dateTaken.compareTo(other.dateTaken) < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * adds a tag to the photo
     * @param type tagType
     * @param value tagValue
     * @throws Exception if tag cannot be added to photo
     */
    public void addTag(String type, String value) throws Exception {
        boolean isSingle;
        try {
            isSingle = Model.currentUser.getTagProperty(type).equals("single");
        } catch (Exception e) {
            throw new Exception("you need to pick tag type from the preset");
        }
        if (this.tags.contains(new Tag(type, value))) {
            throw new Exception("Photo Already Has This Tag");
        }
        boolean atLeastOnce = false;
        for (Tag tag : this.tags) {
            if (tag.type.equals(type)) {
                atLeastOnce = true;
                break;
            }
        }
        if (atLeastOnce) {
            if (!isSingle) {
                this.tags.add(new Tag(type, value));
                return;
            } else {
                throw new Exception("tag type can only have single value");
            }
        }
        this.tags.add(new Tag(type, value));
    }

    /**
     * adds a tag to a photo
     * @param type tagType
     * @param value tagValue
     * @param isSingle tagProperty
     * @throws Exception if tag cannot be added to photo
     */
    public void addTag(String type, String value, boolean isSingle) throws Exception {
        String property = isSingle ? "single" : "multiple";
        if (Model.currentUser.tagPreset.contains(new Pair<>(type, property))) {
            this.addTag(type, value);
            return;
        }
        try {
            Model.currentUser.addToTagPreset(type, isSingle);
            this.addTag(type, value);
        } catch (Exception e) {
            throw new Exception("cannot change tag property");
        }
    }

    /**
     * removes tag from a photo
     * @param type tagType
     * @param value tagValue
     * @throws Exception if tag cannot be removed from a photo
     */
    public void removeTag(String type, String value) throws Exception {
        if (!this.tags.contains(new Tag(type, value))) {
            throw new Exception("Photo Does Not Have This Tag");
        }
        this.tags.remove(new Tag(type, value));
    }
}