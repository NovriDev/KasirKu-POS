package com.novdev.kasirku.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.databinding.ActivityInvoiceBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InvoiceActivity extends AppCompatActivity {
    private ActivityInvoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int totalPrice = getIntent().getIntExtra("total_price", 0);
        int paymentAmount = getIntent().getIntExtra("payment_amount", 0);
        String method = getIntent().getStringExtra("payment_method");
        String customer = getIntent().getStringExtra("customer_name");

        binding.invoiceTotal.setText("Total Belanja : " + FormatRupiah.formatRupiah(totalPrice));
        binding.invoicePaid.setText("Uang Kembali : " + FormatRupiah.formatRupiah(paymentAmount));
        binding.invoiceMethod.setText("Metode Pembayaran : " + method);
        binding.invoiceCustomer.setText("Nama Pelanggan : " + customer);

        int change = paymentAmount - totalPrice;
        binding.invoiceChange.setText("Jumlah Dibayar : "+ FormatRupiah.formatRupiah(change));

        binding.btnDone.setOnClickListener(v -> {
            generateInvoicePdf();
        });
    }

    private void generateInvoicePdf() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        int y = 25;

        paint.setTextSize(14f);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("INVOICE PEMBAYARAN", pageInfo.getPageWidth() / 2, y, paint);

        paint.setFakeBoldText(false);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        y += 30;
        canvas.drawText("Pelanggan  : " + binding.invoiceCustomer.getText().toString(), pageInfo.getPageWidth() / 2, y, paint);
        y += 20;
        canvas.drawText("Metode     : " + binding.invoiceMethod.getText().toString(), pageInfo.getPageWidth() / 2, y, paint);
        y += 20;
        canvas.drawText("Total      : " + binding.invoiceTotal.getText().toString(), pageInfo.getPageWidth() / 2, y, paint);
        y += 20;
        canvas.drawText("Dibayar    : " + binding.invoicePaid.getText().toString(), pageInfo.getPageWidth() / 2, y, paint);
        y += 20;
        canvas.drawText("Kembalian  : " + binding.invoiceChange.getText().toString(), pageInfo.getPageWidth() / 2, y, paint);

        y += 40;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11f);
        canvas.drawText("Terima kasih atas kunjungan dan kepercayaan Anda.", pageInfo.getPageWidth() / 2, y, paint);
        y += 15;
        canvas.drawText("Kami tunggu kedatangan Anda berikutnya!", pageInfo.getPageWidth() / 2, y, paint);

        document.finishPage(page);

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "invoice_" + System.currentTimeMillis() + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            document.close();

            // Buat URI menggunakan FileProvider (pastikan sudah disetup di Manifest)
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", // Ganti sesuai authority di manifest
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Bagikan Invoice"));
            finish();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal simpan invoice", Toast.LENGTH_SHORT).show();
        }


        document.close();
    }


}
