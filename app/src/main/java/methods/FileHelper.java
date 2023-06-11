package methods;


// FileHelper.java

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.btl.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class FileHelper {
    private Activity activity;
    private Button button;
    private String checker = "";
    private Uri fileUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private static final int PICK_FILE_REQUEST = 438;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    public FileHelper(Activity activity, Button button) {
        this.activity = activity;
        this.button = button;
        storageReference = FirebaseStorage.getInstance().getReference().child("Files");
        databaseReference = FirebaseDatabase.getInstance().getReference("Files");
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        setupClickListener();
    }

    private void setupClickListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        CharSequence[] options = {"PDF Files", "MS Word Files", "Text Files"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select the File");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    checker = "pdf";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    activity.startActivityForResult(Intent.createChooser(intent, "Select PDF File"), PICK_FILE_REQUEST);
                } else if (i == 1) {
                    checker = "docx";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    activity.startActivityForResult(Intent.createChooser(intent, "Select MS Word File"), PICK_FILE_REQUEST);
                } else if (i == 2) {
                    checker = "txt";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/plain");
                    activity.startActivityForResult(Intent.createChooser(intent, "Select Text File"), PICK_FILE_REQUEST);
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();

            long fileSize = getFileSize(fileUri);
            if (fileSize > MAX_FILE_SIZE) {
                Toast.makeText(activity, "File size exceeds the limit", Toast.LENGTH_SHORT).show();
                return;
            }

            String fileExtension = getFileExtension(fileUri);

            if (checker.equals("pdf") && fileExtension.equals("pdf")) {
                uploadFile();
            } else if (checker.equals("docx") && fileExtension.equals("docx")) {
                uploadFile();
            } else if (checker.equals("txt") && fileExtension.equals("txt")) {
                uploadFile();

            } else {
                Toast.makeText(activity, "Invalid file type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile() {
        progressDialog.show();
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + getFileExtension(fileUri));
        UploadTask uploadTask = fileRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        String fileName = fileRef.getName();

                        Toast.makeText(activity, fileName, Toast.LENGTH_SHORT).show();
                        new FireStoreMethod().addMessage("", FirebaseAuth.getInstance().getCurrentUser().getUid(),"",fileName,downloadUrl, new Date());

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(activity, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.getContentResolver().getType(uri));
    }

    private long getFileSize(Uri uri) {
        try {
            return activity.getContentResolver().openFileDescriptor(uri, "r").getStatSize();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

        public void startDownload(String downloadUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("Downloading File");
        request.setDescription("Please wait...");

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    // Tải về đã hoàn thành, thực hiện các thao tác cần thiết
                    Toast.makeText(activity, "Download completed", Toast.LENGTH_SHORT).show();
                    // Quay trở lại MainActivity
                    Intent mainIntent = new Intent(activity, MainActivity.class);
                    activity.startActivity(mainIntent);
                }
            }
        };

        IntentFilter downloadCompleteFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        activity.registerReceiver(downloadCompleteReceiver, downloadCompleteFilter);
    }





}
