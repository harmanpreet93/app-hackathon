package app.hackathon.hackathon;

import java.io.File;

/**
 * Created by H173029 on 12/17/2016.
 */

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
