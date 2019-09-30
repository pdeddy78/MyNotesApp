package com.example.latihan.mynotesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.latihan.mynotesapp.db.NoteHelper;
import com.example.latihan.mynotesapp.entity.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.DATE;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.TITLE;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtDescription;
    private Button btnSubmit;

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";

    private boolean isEdit = false;

    public static final int REQUEST_ADD = 100;
    public static final int REQUEST_UPDATE = 200;

    private final int ALERT_DIALOG_CLOSE = 10;

    private Note note;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            note = new Note();
        }

        Uri uri = getIntent().getData();
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) note = new Note(cursor);
                cursor.close();
            }
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Ubah";
            btnTitle = "Update";
            if (note != null) {
                edtTitle.setText(note.getTitle());
                edtDescription.setText(note.getDescription());
            }
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnSubmit.setText(btnTitle);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            boolean isEmpty = false;

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Field can not be blank");
                return;
            }

            note.setTitle(title);
            note.setDescription(description);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, note);
            intent.putExtra(EXTRA_POSITION, position);

            if (!isEmpty) {

                ContentValues values = new ContentValues();
                values.put(TITLE, title);
                values.put(DESCRIPTION, description);

                if (isEdit) {
                    getContentResolver().update(getIntent().getData(), values, null, null);
                    Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil diedit", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    values.put(DATE, getCurrentDate());
                    note.setDate(getCurrentDate());
                    Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil disimpan", Toast.LENGTH_SHORT).show();
                    getContentResolver().insert(CONTENT_URI, values);

                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ALERT_DIALOG_DELETE = 20;
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
            dialogTitle = "Hapus Note";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);
                            Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show();
                            getContentResolver().delete(getIntent().getData(), null, null);
                            finish();
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
