/**
 * @author Burak  burakkcoskun@hotmail.com burak.coskun7@ogr.sakarya.edu.tr
 * @since 1.4.2024
 * <p>
 * Main sınıfı: projedeki Github URL sini kullanıcıdan isteyen, deponun içerisinin boş olup olmadığını kontrol edip eğer boş ise uyarı mesajı gönderen sınıf..
 * </p>
 */


package main;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import analyzer.JavaAnalyzer;
import github.GithubDepo;
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("GitHub Depo URL'sini girin: ");
        String repoUrl = scanner.nextLine();

        String tempFolderPath = GithubDepo.cloneRepository(repoUrl);

        if (tempFolderPath != null) {
            List<File> javaFiles = GithubDepo.getJavaFiles(tempFolderPath);

            if (javaFiles.isEmpty()) {
                System.out.println("Depoda *.java uzantılı dosya bulunamadı.");
            } else {
                for (File javaFile : javaFiles) {
                    JavaAnalyzer.analyzeJavaFile(javaFile);
                }
            }
        } else {
            System.out.println("Depo klonlanırken bir hata oluştu.");
        }

        scanner.close();
    }
}
