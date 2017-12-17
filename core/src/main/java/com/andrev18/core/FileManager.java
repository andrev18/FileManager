package com.andrev18.core;

import android.app.Application;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.SingleSubject;

/**
 * Created by avlad on 17.12.2017.
 */

public class FileManager {
    private final Application application;

    public FileManager(Application application) {
        this.application = application;
    }

    public Completable writeFile(String filename, InputStream inputStream) {
        CompletableSubject subject = CompletableSubject.create();
        byte[] b = new byte[1024];
        int c;

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = application.openFileOutput(filename, Context.MODE_PRIVATE);
            while ((c = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, c);
            }
        } catch (IOException e) {
            subject.onError(e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!subject.hasThrowable()) {
                subject.onComplete();
            }
        }
        return Completable.fromObservable(subject.toObservable());
    }


    public SingleSubject<byte[]> readFile(String filename) {
        SingleSubject<byte[]> subject = SingleSubject.create();

        FileInputStream fileInputStream = null;
        byte[] result = new byte[0];

        try {
            fileInputStream = application.openFileInput(filename);
            result = new byte[(int) fileInputStream.getChannel().size()];
            fileInputStream.read(result);
        } catch (FileNotFoundException e) {
            subject.onError(e);
        } catch (IOException e) {
            subject.onError(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!subject.hasThrowable()) {
                subject.onSuccess(result);
            }
        }

        return subject;
    }
}
