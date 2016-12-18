package app.hackathon.hackathon;

import android.os.Environment;

import java.io.File;

/**
 * Created by H173029 on 12/17/2016.
 */

public class AlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }

}
