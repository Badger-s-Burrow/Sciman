/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


package de.badgersburrow.sciman.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.Entry;
//import com.dropbox.client2.DropboxAPI.DownloadRequest;
//import com.dropbox.client2.DropboxAPI.FileDownload;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import de.badgersburrow.sciman.R;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class SyncFullLibraryinBG extends AsyncTask<Void, Long, Boolean> {


    private Context mContext;
    private final ProgressDialog mDialog;
    private DropboxAPI<?> mApi;
    private String mPath;
    private String lPath;
    private Drawable mDrawable;

    private FileOutputStream mFos;
    //private DownloadRequest mRequest;

    private boolean mCanceled;
    private Long mFileLen;
    private String mErrorMsg;
    private long filesSize;
    private long loadedFilesSize;
    private String currentFile;
    
    //private Context context;
    private NotificationManager nm;
    private Notification noti;
    private final int STATUS_BAR_NOTIFICATION = 1;
    private boolean mRun;
    

    // Note that, since we use a single file name here for simplicity, you
    // won't be able to use this code for two simultaneous downloads.
    //private final static String IMAGE_FILE_NAME = "dbroulette.png";

    public SyncFullLibraryinBG(Context context, DropboxAPI<?> api, String dropboxPath, String localPath) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        filesSize = 0;
        loadedFilesSize = 0;
        mApi = api;
        mPath = "/" + dropboxPath + "/";//Name of the bibliography 
        lPath = localPath;
        
        ///////////////
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        CharSequence tickerText = "hello";
        long when = System.currentTimeMillis();
        noti = new Notification(R.drawable.ic_launcher, tickerText, when);
        //context = this.getApplicationContext();
        
        Intent notiIntent = new Intent(context, SyncFullLibraryinBG.class);
        PendingIntent pi = PendingIntent.getService(context, 0, notiIntent, 0);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        
        CharSequence title = "Downloading initializing...";
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.syncing_notification);
		 contentView.setImageViewResource(R.id.status_icon, R.drawable.logo);
		 contentView.setTextViewText(R.id.status_text, title);
		 contentView.setProgressBar(R.id.status_progress, 100, 0, false);
		 noti.contentView = contentView;
		 noti.contentIntent = pi;
        
        nm.notify(STATUS_BAR_NOTIFICATION, noti);
        
        ///////////////

        mDialog = new ProgressDialog(context);
        mDialog.setTitle(mContext.getString(R.string.dropb_downloading));
        mDialog.setMessage(mContext.getString(R.string.dropb_collfiles));
        mDialog.setMax(100);
        //mDialog.setMessage("Uploading " + file.getName());
        //I would like to update the message, showing always the current file
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setButton(mContext.getString(R.string.cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCanceled = true;
                mErrorMsg = mContext.getString(R.string.dropb_error_canceled);

                // This will cancel the getThumbnail operation by closing
                // its stream
                if (mFos != null) {
                    try {
                        mFos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });

        mDialog.show();
    }
    
  

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (mCanceled) {
            	mRun = false;
                return false;
                
            }

            // Get the metadata for a directory
            Entry dirent = mApi.metadata(mPath, 1000, null, true, null);

            if (!dirent.isDir ) {
                // It's not a directory, or there's nothing in it
            	mErrorMsg = mPath + mContext.getString(R.string.dropb_error_notdir);
                return false;
            }
            if ( dirent.contents == null) {
                // It's not a directory, or there's nothing in it
            	
                mErrorMsg = mContext.getString(R.string.dropb_error_dirempty);
                return false;
            }

            // Make a list of everything we would like to download
            ArrayList<Entry> files = new ArrayList<Entry>();
            int numberOfRelevantFiles=0;
            for (Entry ent: dirent.contents) {
                if ((ent.fileName().endsWith(".pdf"))) {
                	numberOfRelevantFiles+=1;
                    // Add it to the list if online pdf is newer than local
                	File localTestFile = new File(lPath + ent.fileName());
                	if (!localTestFile.exists()){
                		files.add(ent);
                        filesSize+=ent.bytes;
                	}
                	else if (localTestFile.lastModified()/1000<calcTimestringToLong(ent.modified)){
                		files.add(ent);
                        filesSize+=ent.bytes;
                	}
                    
                } else if (ent.fileName().endsWith(".bib")) {
                	numberOfRelevantFiles+=1;
                    // Add it to the list 
                	File localTestFile = new File(lPath + ent.fileName());
                	if (!localTestFile.exists()){
                		files.add(ent);
                        filesSize+=ent.bytes;
                	}
                	else if (localTestFile.lastModified()/1000<calcTimestringToLong(ent.modified)){
                		files.add(ent);
                        filesSize+=ent.bytes;
                	}
                }
                
            }

            if (mCanceled) {
                return false;
            }

            if (files.size() == 0 && numberOfRelevantFiles==0) {
                // No thumbs in that directory
                mErrorMsg = mContext.getString(R.string.dropb_error_dirempty);
                return false;
            } else if (files.size() == 0 && numberOfRelevantFiles!=0){
            	mErrorMsg = mContext.getString(R.string.dropb_error_uptodate);
                return false;    	
            }

            // Now pick a random one
            // In my case pick everything
            //int index = (int)(Math.random() * thumbs.size());
            //Entry ent = thumbs.get(index);
            //should i not loop over files??
            if (!lPath.endsWith("/")){
            	lPath=lPath + "/";
            }
            if (!lPath.startsWith("/")){
            	mErrorMsg = mContext.getString(R.string.dropb_error_dirnotvalid);
                return false;
            }
            	
            File folder = new File(lPath);
            boolean success = false;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (!success) {
                // Do something on success
            } else {
                // Do something else on failure 
            }

            
            for (Entry ent: files) {
            	String path = ent.path;
                mFileLen = ent.bytes;
                String FileName = ent.fileName();
                currentFile = ent.fileName();
                //showToast(path);
                
                String cachePath = lPath+ FileName; 
                try {
                    mFos = new FileOutputStream(cachePath);
                } catch (FileNotFoundException e) {
                    mErrorMsg = mContext.getString(R.string.dropb_error_couldnotcreate);
                    return false;
                }

                // This downloads a smaller, thumbnail version of the file.  The
                // API to download the actual file is roughly the same.
                mApi.getFile(path, null, mFos, new ProgressListener() {
                    @Override
                    public long progressInterval() {
                        // Update the progress bar every half-second or so
                        return 500;
                    }

                    @Override
                    public void onProgress(long bytes, long total) {
                        publishProgress(bytes);
                    }
                });//getThumbnail(path, mFos, ThumbSize.BESTFIT_960x640,ThumbFormat.JPEG, null);
                if (mCanceled) {
                    return false;
                }
                loadedFilesSize+=ent.bytes;
                
            }
            
            

            //mDrawable = Drawable.createFromPath(cachePath);
            // We must have a legitimate picture
            return true;

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = mContext.getString(R.string.dropb_error_downcanc);
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            	
            	try{
            	Entry createFolder = mApi.createFolder(mPath);

            	mErrorMsg = mContext.getString(R.string.dropb_error_foldercreated);
            	return false;
            	} catch(DropboxException e2){
            		mErrorMsg = mContext.getString(R.string.dropb_error_foldernotcreated1) + mPath + mContext.getString(R.string.dropb_error_foldernotcreated2);
            		return false;
            	}
            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                // can't be thumbnailed
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = mContext.getString(R.string.dropb_error_network);
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = mContext.getString(R.string.dropb_error_dropbox);
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = mContext.getString(R.string.dropb_error_unkown);
        }
        return false;
    }

    

    
    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int)(100.0*(double)(loadedFilesSize + progress[0])/filesSize + 0.5);
        CharSequence title = "Downloading: " + percent + "%";
        noti.contentView.setTextViewText(R.id.status_text, title);
        noti.contentView.setProgressBar(R.id.status_progress, 100, percent, false);
    	nm.notify(STATUS_BAR_NOTIFICATION, noti);
        //mDialog.setProgress(percent);
        //mDialog.setMessage(currentFile);
    }

    @Override
    protected void onPostExecute(Boolean result) {
    	mRun = false;//mDialog.dismiss();
        if (result) {
            showToast(mContext.getString(R.string.dropb_updated));
        } else {
            showToast(mErrorMsg);
        }
    }
    
    private long calcTimestringToLong(String Timestring){
    	//Timestring  "EEE, dd MMM yyyy kk:mm:ss ZZZZZ"
    	long epoch =900000000;
    	try {
    	String Time = Timestring.substring(5,25);
    	epoch = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").parse(Time).getTime()/1000;
    	
    	} catch (ParseException e)
    	  {System.out.println("Exception :"+e);  }  
    	 
    	
    	return epoch;
    	
    }
    
    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }


}
