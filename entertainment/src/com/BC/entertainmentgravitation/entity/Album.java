package com.BC.entertainmentgravitation.entity;

import java.util.List;

public class Album {
	private List<Photo_images> More_pictures;

	private List<Photo_images> Photo_images;

	private List<Photo_images> photographs;

	public void setMore_pictures(List<Photo_images> More_pictures) {
		this.More_pictures = More_pictures;
	}

	public List<Photo_images> getMore_pictures() {
		return this.More_pictures;
	}

	public void setPhoto_images(List<Photo_images> Photo_images) {
		this.Photo_images = Photo_images;
	}

	public List<Photo_images> getPhoto_images() {
		return this.Photo_images;
	}

	public void setPhotographs(List<Photo_images> photographs) {
		this.photographs = photographs;
	}

	public List<Photo_images> getPhotographs() {
		return this.photographs;
	}
}
