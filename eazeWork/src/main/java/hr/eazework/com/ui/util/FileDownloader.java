package hr.eazework.com.ui.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class FileDownloader extends AsyncTask<String, Integer, Boolean> {

	private FileDownloadListener downloadListener;
	private Context context;
	private File vodafile;

	public FileDownloader(Context context, FileDownloadListener downloadListener) {
		this.context = context;
		this.downloadListener = downloadListener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String url = params[0];
		String fileName = params[1];
		boolean downloadStatus = downLoadFileFromNetwork(url, fileName);
		return downloadStatus;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			if (downloadListener != null) {
				downloadListener.onFileDownloadComplete(result, "");
			} else {
				Toast.makeText(context, "File downloaded successfully",
						Toast.LENGTH_LONG).show();
			}
		} else {
			if (downloadListener != null) {
				downloadListener.onFileDownloadComplete(result, "");
			} else {
				Toast.makeText(context, "File downloaded failed",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean downLoadFileFromNetwork(String downloadUrl, String fileName) {
		boolean bm = false;
		if (!createFileOnDirectroy(fileName)) {
			return false;
		}
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = new FileOutputStream(vodafile);

			int current = 0;
			while ((current = bis.read()) != -1) {
				fos.write(current);
			}

			fos.flush();
			fos.close();
			bm = true;
		} catch (MalformedURLException e) {
			bm = false;
		} catch (OutOfMemoryError e) {
			bm = false;
		} catch (IOException e) {
			bm = false;
		} catch (Exception e) {
			bm = false;
		}
		return bm;
	}

	private boolean createFileOnDirectroy(String fileName) {
		boolean isDir = false;
		File directory = null;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			directory = new File(Environment.getDownloadCacheDirectory()
					+ File.separator + "MyVodafone");
			if (directory.exists()) {
				isDir = true;
			} else {
				if (directory.mkdirs()) {
					isDir = true;
				} else {
					isDir = false;
				}
			}
		} else {
			directory = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "MyVodafone");
			if (directory.exists()) {
				isDir = true;
			} else {
				if (directory.mkdirs()) {
					isDir = true;
				} else {
					isDir = false;
				}
			}
		}

		if (isDir && directory != null) {
			vodafile = new File(directory, fileName);
			if(vodafile!=null)
				return true;
			else
				return false;
		} else {
			return false;
		}
	}
}
