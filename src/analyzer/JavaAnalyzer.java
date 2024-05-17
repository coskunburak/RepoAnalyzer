/**
 * @author Burak  burakkcoskun@hotmail.com burak.coskun7@ogr.sakarya.edu.tr
 * @since 1.4.2024
 * <p>
 * JavaAnalyzer sınıfı: projedeki analiz değerlerini hesaplayan, bulan ekrana getiren sınıf.
 * </p>
 */




package analyzer;

import java.io.BufferedReader;
import java.util.Scanner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
public class JavaAnalyzer {

    public static void analyzeJavaFile(File javaFile) {
        System.out.println("-----------------------------------------");
        System.out.println("Sınıf: " + javaFile.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(javaFile))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            String content = contentBuilder.toString();

            int javadocLines = countJavadocLines(content);
            System.out.println("Javadoc Satır Sayısı: " + javadocLines);
            
            int multiCommentLines = countMultiCommentLines(content);
            int otherCommentLines = countSingleCommentLines(content);
            int totalCommentLines = multiCommentLines + otherCommentLines;
            System.out.println("Yorum Satırı Sayısı: " + (totalCommentLines));
            
            int codeLines = countCodeLines(content);
            System.out.println("Kod Satırı Sayısı: " + codeLines);

            int loc = countLOC(content);
            System.out.println("LOC: " + loc);

            int functionCount = countFunction(content);
            System.out.println("Fonksiyon Sayısı: " + functionCount);

            double yorumSapmaYuzdesi = calculateYorumSapmaYuzdesi(javadocLines, totalCommentLines, codeLines, functionCount);
            System.out.println("Yorum Sapma Yüzdesi: %" + yorumSapmaYuzdesi);

        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + e.getMessage());
        }
    }

    private static int countJavadocLines(String content) {
        Pattern pattern = Pattern.compile("/\\*\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*/");
        Matcher matcher = pattern.matcher(content);

        int count = 0;
        while (matcher.find()) {
            String javadocBlock = matcher.group();
            // Başlangıç ve bitiş işaretlerini saymadan içeriği hesapla
            int linesInBlock = javadocBlock.split("[\\r\\n]+").length - 2; // Başlangıç ve bitiş satırlarını çıkart
            count += linesInBlock;
        }
        return count;
    }
    
    private static int countSingleCommentLines(String content) {
        // Tek satırlı yorumları bulmak için regex deseni
        Pattern singleLineCommentPattern = Pattern.compile("//.*");

        // Matcher oluştur
        Matcher singleLineMatcher = singleLineCommentPattern.matcher(content);

        // Toplam tek satırlı yorum satırı sayısı
        int totalSingleCommentLines = 0;

        // Tek satırlı yorumları say
        while (singleLineMatcher.find()) {
            totalSingleCommentLines++;
        }

        return totalSingleCommentLines;
    }
    
    private static int countMultiCommentLines(String content) {
        // Çoklu yorumları bulmak için regex deseni
        Pattern multiLineCommentPattern = Pattern.compile("/\\*[^*]*\\*+(?:[^*/][^*]*\\*+)*/", Pattern.DOTALL);

        // Javadoc'u bulmak için regex deseni
        Pattern javadocPattern = Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL);

        // Matcher'ları oluştur
        Matcher multiLineMatcher = multiLineCommentPattern.matcher(content);
        Matcher javadocMatcher = javadocPattern.matcher(content);

        // Toplam çoklu yorum satırı sayısı
        int totalMultiCommentLines = 0;

        // Çoklu yorum satırlarını saymak için döngü
        while (multiLineMatcher.find()) {
            // Javadoc'u eşleşen çoklu yorum satırları arasında kontrol et
            String match = multiLineMatcher.group();
            javadocMatcher.region(multiLineMatcher.start(), multiLineMatcher.end());
            if (!javadocMatcher.find()) {
                // Eşleşen çoklu yorum satırı javadoc değilse sayacı artır
                totalMultiCommentLines++;
            }
        }

        return totalMultiCommentLines;
    }
    
    private static int countCodeLines(String content) {
        String[] lines = content.split("\r\n|\r|\n");
        int count = 0;
        boolean inComment = false;
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (!inComment) {
                    if (!line.startsWith("//")) {
                        if (!line.startsWith("/*")) {
                            count++;
                        } else {
                            inComment = true;
                        }
                    }
                }
                if (inComment && line.endsWith("*/")) {
                    inComment = false;
                }
            }
        }
        return count;
    }

    private static int countLOC(String content) {
        String[] lines = content.split("\r\n|\r|\n");
        int count = 0;
        for (String line : lines) {
            line = line.trim();
            count++;
        }
        return count;
    }
    public static int countFunction(String content) {
        // Fonksiyon deseni
        String functionPattern = "(?:(public|private|protected|static|\\s)+[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\s*\\([^\\)]*\\)\\s*\\{)";

        // Desenin derlenmesi
        Pattern pattern = Pattern.compile(functionPattern);

        // Fonksiyonları saymak için kullanılacak sayaç
        int count = 0;

        // Metin üzerinde fonksiyonları tespit et
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String accessModifier = matcher.group(1);
            String returnType = matcher.group(2);

            // Eğer erişim belirleyici ve dönüş türü dolu ise, say
            if (accessModifier != null && returnType != null) {
                count++;
            }
        }

        return count;
    }


    private static double calculateYorumSapmaYuzdesi(int javadocLines, int totalCommentLines, int codeLines, int functionCount) {
        // Yorum Grubu (YG) ve Kod Grubu (YH) hesaplamaları
        double YG = ((javadocLines + totalCommentLines) * 0.8) / functionCount;
        double YH = ((double) codeLines / functionCount) * 0.3;
        
        // Sıfıra bölme hatası kontrolü
        if (functionCount == 0 || YH == 0) {
            return 0.0;
        }
        
        // Yorum Sapma Yüzdesi hesabı
        double result = ((100 * YG) / YH) - 100;
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return 0.0;
        }
        return result;
    }
}

