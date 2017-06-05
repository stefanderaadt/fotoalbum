package nl.hu.fotoalbum.services;

public class ServiceProvider {
	private static AlbumService albumService = new AlbumService();
	private static PictureService pictureService = new PictureService();
	private static UserService userService = new UserService();

	public static PictureService getPictureService() {
		return pictureService;
	}

	public static UserService getUserService() {
		return userService;
	}

	public static AlbumService getAlbumService() {
		return albumService;
	}
}