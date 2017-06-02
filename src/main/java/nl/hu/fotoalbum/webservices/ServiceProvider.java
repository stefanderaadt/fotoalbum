package nl.hu.fotoalbum.webservices;

public class ServiceProvider {
	private static AlbumService albumService = new AlbumService();

	public static AlbumService getAlbumService() {
		return albumService;
	}
}