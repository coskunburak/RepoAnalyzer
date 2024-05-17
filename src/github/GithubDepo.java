/**
 * @author Burak Coşkun   burakkcoskun@hotmail.com burak.coskun7@ogr.sakarya.edu.tr
 * @since 1.4.2024
 * <p>
 * GithubDepo sınıfı: projedeki klonlama, yalnızca sınıf türündeki dosyaları ayıklamayı yapan sınıf.
 * </p>
 */



package github;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GithubDepo {

    public static String cloneRepository(String repoUrl) {
        String tempFolderName = "tempRepo";
        Path tempFolderPath = Paths.get(System.getProperty("user.dir"), tempFolderName);

        try {
            // Git klonlama komutunu çalıştır
            Process process = Runtime.getRuntime().exec("git clone " + repoUrl + " " + tempFolderName);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Depo klonlandı. Klonlanan dizin: " + tempFolderPath.toString());
                return tempFolderPath.toString();
            } else {
                System.out.println("Depo klonlanırken bir hata oluştu. Çıkış kodu: " + exitCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Depo klonlanırken bir hata oluştu: " + e.getMessage());
            return null;
        }
    }

    public static List<File> getJavaFiles(String folderPath) {
        List<File> javaFiles = new ArrayList<>();
        File folder = new File(folderPath);
        findJavaFiles(folder, javaFiles);
        return javaFiles;
    }

    private static void findJavaFiles(File folder, List<File> javaFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java") && !isInterfaceOrEnum(file)) {
                    javaFiles.add(file);
                } else if (file.isDirectory()) {
                    findJavaFiles(file, javaFiles);
                }
            }
        }
    }

    private static boolean isInterfaceOrEnum(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            // İçerikte "interface" veya "enum" anahtar kelimelerini ara
            return content.contains("interface") || content.contains("enum");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
