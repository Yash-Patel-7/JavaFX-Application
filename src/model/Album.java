package model;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Class for albums
 * @author Parth Patel, Yash Patel
 */
public final class Album implements java.io.Serializable, Comparable<Album> {
    /**
     * unique ID for serialization
     */
    @Serial
    private static final long serialVersionUID = -2523531824640650719L;
    /**
     * name of album
     */
    public String name;
    /**
     * list of photos in album
     */
    public ArrayList<Photo> photos;
    /**
     * start time of the earliest photo in album
     */
    public Calendar start;
    /**
     * start time of the latest photo in album
     */
    public Calendar end;

    /**
     * constructor to create empty album
     * @param name name of empty album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
        this.start = Calendar.getInstance();
        this.start.set(Calendar.MILLISECOND, 0);
        this.end = this.start;
    }

    /**
     * constructor to create album with photos
     * @param name name of album
     * @param photos photos to store in album
     */
    public Album(String name, ArrayList<Photo> photos) {
        this.name = name;
        this.photos = photos;
        if (photos.isEmpty()) {
            this.start = Calendar.getInstance();
            this.start.set(Calendar.MILLISECOND, 0);
            this.end = this.start;
            return;
        }
        this.start = Collections.min(photos).dateTaken;
        this.end = Collections.max(photos).dateTaken;
    }

    /**
     * checks if two albums are equal
     * @param obj object to compare album with
     * @return if albums are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Album other)) {
            return false;
        }
        return this.name.equals(other.name);
    }

    /**
     * compares one album with another
     * @param other the object to be compared.
     * @return value to represent order of albums
     */
    public int compareTo(Album other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.name.compareToIgnoreCase(other.name) < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * adds a photo to an album
     * @param path path of the photo to be added
     * @throws Exception if album already has that photo
     */
    public void addPhoto(String path) throws Exception {
        if (this.photos.contains(new Photo(path))) {
            throw new Exception("Photo is already in album");
        }
        Model.currentUser.uniquePhotos.putIfAbsent(path, new Photo(path));
        Photo newPhoto;
        newPhoto = Model.currentUser.uniquePhotos.get(path);
        this.photos.add(newPhoto);
        if (this.photos.size() == 1) {
            this.start = newPhoto.dateTaken;
            this.end = this.start;
            return;
        }
        if (newPhoto.dateTaken.compareTo(this.start) < 0) this.start = newPhoto.dateTaken;
        if (newPhoto.dateTaken.compareTo(this.end) > 0) this.end = newPhoto.dateTaken;
    }

    /**
     * adds a photo with a caption to an album
     * @param path path of the photo to be added
     * @param caption caption of the photo to be added
     * @throws Exception if photo already exists in album
     */
    public void addPhoto(String path, String caption) throws Exception {
        if (this.photos.contains(new Photo(path))) {
            throw new Exception("Photo is already in album");
        }
        Model.currentUser.uniquePhotos.putIfAbsent(path, new Photo(path, caption));
        Photo newPhoto = Model.currentUser.uniquePhotos.get(path);
        this.photos.add(newPhoto);
        if (this.photos.size() == 1) {
            this.start = newPhoto.dateTaken;
            this.end = this.start;
        }
        if (newPhoto.dateTaken.compareTo(this.start) < 0) this.start = newPhoto.dateTaken;
        if (newPhoto.dateTaken.compareTo(this.end) > 0) this.end = newPhoto.dateTaken;
    }

    /**
     * removes photo from album
     * @param path path of the photo to be removed
     * @throws Exception if photo is not in album
     */
    public void removePhoto(String path) throws Exception {
        if (!this.photos.contains(new Photo(path))) {
            throw new Exception("Photo not in album");
        }
        this.photos.remove(new Photo(path));
        if (this.photos.isEmpty()) {
            this.start = Calendar.getInstance();
            this.start.set(Calendar.MILLISECOND, 0);
            this.end = this.start;
            return;
        }
        Photo photo = Model.currentUser.uniquePhotos.get(path);
        if (photo.dateTaken.compareTo(this.start) == 0) this.start = Collections.min(this.photos).dateTaken;
        if (photo.dateTaken.compareTo(this.end) == 0) this.end = Collections.max(this.photos).dateTaken;
    }
}
