package com.martinatanasov.colornotebook;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    TextView bookTitle, bookAuthor, pagesInput;
    Button btnUpdate, btnDelete;
    String id, title, author, pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);

        bookTitle =findViewById(R.id.bookTitle2);
        bookAuthor =findViewById(R.id.bookAuthor2);
        pagesInput =findViewById(R.id.pagesInput2);
        btnUpdate =findViewById(R.id.btnUpdate);
        btnDelete =findViewById(R.id.btnDelete);

        //First before update DB
        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab =getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update DB
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                title = bookTitle.getText().toString().trim();
                author = bookAuthor.getText().toString().trim();
                pages = pagesInput.getText().toString().trim();
                myDB.updateData(id, title, author, pages);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });

    }

    void getAndSetIntentData(){
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")
                && getIntent().hasExtra("author") && getIntent().hasExtra("pages")){
            // Getting data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            pages = getIntent().getStringExtra("pages");

            //Setting Intent data
            bookTitle.setText(title);
            bookAuthor.setText(author);
            pagesInput.setText(pages);

        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }

    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + "?");
        builder.setMessage("Are you sure you want to delete " + title + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteDataOnOneRow(id);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}