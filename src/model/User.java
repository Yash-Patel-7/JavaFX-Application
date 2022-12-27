package model;

import javafx.util.Pair;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Predicate;

/**
 * class for users
 * @author Parth Patel, Yash Patel
 */
public final class User implements java.io.Serializable, Comparable<User> {
    /**
     * unique id for serialization
     */
    @Serial
    private static final long serialVersionUID = -379318737058451008L;
    /**
     * the user's username
     */
    public String username;
    /**
     * the user's albums
     */
    public ArrayList<Album> albums;
    /**
     * the tag presets for the user
     */
    public ArrayList<Pair<String, String>> tagPreset;
    /**
     * the user's photos
     */
    public HashMap<String, Photo> uniquePhotos;

    /**
     * creates a user
     * @param username new user's username
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
        this.tagPreset = new ArrayList<>();
        this.tagPreset.add(new Pair<>("Other", "Custom"));
        this.uniquePhotos = new HashMap<>();
    }

    /**
     * checks if two objects are equal
     * @param obj other object
     * @return if the two users are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User other)) {
            return false;
        }
        return this.username.equals(other.username);
    }

    /**
     * compares two users
     * @param other the object to be compared.
     * @return the order of the two users
     */
    public int compareTo(User other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.username.compareToIgnoreCase(other.username) < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * adds a preset to the user's tag presets
     * @param type tagType
     * @param isSingle tagProperty
     * @throws Exception if preset cannot be added
     */
    public void addToTagPreset(String type, boolean isSingle) throws Exception {
        String property = isSingle ? "single" : "multiple";
        for (Pair<String, String> p : this.tagPreset) {
            if (p.getKey().equals(type)) {
                throw new Exception("tag already exists in preset");
            }
        }
        this.tagPreset.add(new Pair<>(type, property));
    }

    /**
     * gets tag property
     * @param type tagType
     * @return tagProperty
     * @throws Exception if tag is not in preset
     */
    public String getTagProperty(String type) throws Exception {
        for (Pair<String, String> p : this.tagPreset) {
            if (p.getKey().equals(type)) {
                return p.getValue();
            }
        }
        throw new Exception("could not get property because tag is not in preset");
    }

    /**
     * creates an album
     * @param name album's name
     * @throws Exception if album cannot be added
     */
    public void createAlbum(String name) throws Exception {
        if (this.albums.contains(new Album(name))) {
            throw new Exception("Album Already Exists");
        }
        this.albums.add(new Album(name));
    }

    /**
     * creates an album with photos
     * @param name album's name
     * @param photos album's photos
     * @throws Exception if album cannot be created
     */
    public void createAlbum(String name, ArrayList<Photo> photos) throws Exception {
        if (this.albums.contains(new Album(name))) {
            throw new Exception("Album Already Exists");
        }
        this.albums.add(new Album(name, photos));
    }

    /**
     * deletes an album
     * @param name name of album to delete
     * @throws Exception if album cannot be deleted
     */
    public void deleteAlbum(String name) throws Exception {
        if (!this.albums.contains(new Album(name))) {
            throw new Exception("Album Not Found");
        }
        this.albums.remove(new Album(name));
    }

    /**
     * renames an album
     * @param oldName album name to rename
     * @param newName new name
     * @throws Exception if album cannot be renamed
     */
    public void renameAlbum(String oldName, String newName) throws Exception {
        if (!this.albums.contains(new Album(oldName))) {
            throw new Exception("Album Not Found");
        }
        if (this.albums.contains(new Album(newName))) {
            throw new Exception("Album Already Exists");
        }
        this.albums.get(this.albums.indexOf(new Album(oldName))).name = newName;
    }

    /**
     * gets all the photos that belong to user
     * @return all photos
     */
    public ArrayList<Photo> getAllPhotos() {
        return new ArrayList<>(this.uniquePhotos.values());
    }

    /**
     * gets filtered photos
     * @param predicate filter
     * @return filtered photos
     */
    public ArrayList<Photo> getPhotos(Predicate<? super Photo> predicate) {
        ArrayList<Photo> allPhotos = getAllPhotos();
        ArrayList<Photo> result = new ArrayList<>();
        for (Photo p : allPhotos) {
            if (predicate.test(p)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * filters photos by tag
     * @param type tagType
     * @param value tagValue
     * @return photos filtered by tag
     */
    public ArrayList<Photo> getPhotosByTag(String type, String value) {
        Predicate<Photo> containsTag = p -> p.tags.contains(new Tag(type, value));
        return getPhotos(containsTag);
    }

    /**
     * filters photos by two tags
     * @param type1 tagType1
     * @param value1 tagValue1
     * @param type2 tagType2
     * @param value2 tagValue2
     * @param isAND if filtering by and/or
     * @return photos filtered by tags
     */
    public ArrayList<Photo> getPhotosByTags(String type1, String value1, String type2, String value2, boolean isAND) {
        Predicate<Photo> containsTag1 = p -> p.tags.contains(new Tag(type1, value1));
        Predicate<Photo> containsTag2 = p -> p.tags.contains(new Tag(type2, value2));
        Predicate<Photo> containsTags;
        if (isAND) {
            containsTags = containsTag1.and(containsTag2);
        } else {
            containsTags = containsTag1.or(containsTag2);
        }
        return getPhotos(containsTags);
    }

    /**
     * filters photos by a range
     * @param start start time
     * @param end end time
     * @return photos filtered by time
     */
    public ArrayList<Photo> getPhotosInRange(Calendar start, Calendar end) {
        Predicate<Photo> inRange = p -> p.dateTaken.compareTo(start) >= 0 && p.dateTaken.compareTo(end) <= 0;
        return getPhotos(inRange);
    }
}


