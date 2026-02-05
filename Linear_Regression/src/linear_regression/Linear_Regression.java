
package linear_regression;

import java.io.*;
import java.util.*;

public class Linear_Regression {

    public static void main(String[] args) {
        String dosyaAdi = "Fish.csv";
        List<double[]> veriSeti = csvOku(dosyaAdi);

        Collections.shuffle(veriSeti);
        int egitimBoyutu = (int)(veriSeti.size() * 0.7);
        List<double[]> egitim = veriSeti.subList(0, egitimBoyutu);
        List<double[]> test = veriSeti.subList(egitimBoyutu, veriSeti.size());

        double[] korelasyonlar = new double[5];
        for (int i = 0; i < 5; i++) {
            korelasyonlar[i] = korelasyon(egitim, i);
            System.out.printf("r%d = %.4f\n", i+1, korelasyonlar[i]);
        }

        int enYuksekIndeks = enYuksekKorelasyon(korelasyonlar);
        System.out.println("En yuksek korelasyona sahip x" + (enYuksekIndeks + 1));

        double[] regresyon = regresyonKatsayilari(egitim, enYuksekIndeks);
        System.out.printf("Regresyon modeli: Y = %.4f + %.4f * x%d\n", regresyon[0], regresyon[1], enYuksekIndeks+1);
        
        System.out.println("\nTest Seti Tahminleri:");
        tahminleriYazdir(test, regresyon, enYuksekIndeks);

        double egitimSSE = sseHesapla(egitim, regresyon, enYuksekIndeks);
        System.out.printf("Egitim SSE: %.4f\n", egitimSSE);

        double testSSE = sseHesapla(test, regresyon, enYuksekIndeks);
        System.out.printf("Test SSE: %.4f\n", testSSE);
    }

    public static List<double[]> csvOku(String dosyaAdi) {
        List<double[]> veri = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dosyaAdi))) {
            String satir;
            boolean ilkSatir = true;
            while ((satir = br.readLine()) != null) {
                if (ilkSatir){
                    ilkSatir= false;
                    continue;
                }
                String[] parcalar = satir.split(",");
               
                try {
                    double Y = Double.parseDouble(parcalar[1]);
                    double X1 = Double.parseDouble(parcalar[2]);
                    double X2 = Double.parseDouble(parcalar[3]);
                    double X3 = Double.parseDouble(parcalar[4]);
                    double X4 = Double.parseDouble(parcalar[5]);
                    double X5 = Double.parseDouble(parcalar[6]);
                    veri.add(new double[]{X1, X2, X3, X4, X5, Y});
                } catch (NumberFormatException e) {
                    System.out.println("hata: "+ e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Dosya okunamadı: " + e.getMessage());
        }
        return veri;
    }

    public static double ortalama(List<double[]> veri, int index) {
        double toplam = 0;
    
        for(int i=0; i<veri.size(); i++){
            double[] v = veri.get(i);
            toplam += v[index];
        }
        return toplam / veri.size();
    }

    public static double korelasyon(List<double[]> veri, int xi) {
        double xOrt = ortalama(veri, xi);
        double yOrt = ortalama(veri, 5);

        double pay = 0, paydaX = 0, paydaY = 0;
        for (int i = 0; i < veri.size(); i++) {
            double[] v = veri.get(i);
            double x = v[xi] - xOrt;
            double y = v[5] - yOrt;
            pay += x * y;
            paydaX += x * x;
            paydaY += y * y;
        }
        return pay / Math.sqrt(paydaX * paydaY);
    }

    public static int enYuksekKorelasyon(double[] korelasyonlar) {
        int maxIndex = 0;
        for (int i = 1; i < korelasyonlar.length; i++) {
            if (Math.abs(korelasyonlar[i]) > Math.abs(korelasyonlar[maxIndex])) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static double[] regresyonKatsayilari(List<double[]> veri, int xi) {
        double xOrt = ortalama(veri, xi);
        double yOrt = ortalama(veri, 5);

        double pay = 0, payda = 0;
        
        for(int i=0; i<veri.size(); i++){
            double[] v= veri.get(i);
            pay+= (v[xi]- xOrt)* (v[5]-yOrt);
            payda += (v[xi]- xOrt)* (v[xi]- xOrt);
        }
        
        double b = pay / payda;
        double a = yOrt - b * xOrt;
        return new double[]{a, b};
    }
    
    public static double sseHesapla(List<double[]> veri, double[] regresyon, int xi) {
        double sse = 0;
        
        for(int i=0; i<veri.size(); i++){
            double[] v= veri.get(i);
            double tahmin= regresyon[0] + regresyon[1]* v[xi];
            sse += Math.pow(v[5]- tahmin, 2);
        }
        return sse;
    }
    
    public static void tahminleriYazdir(List<double[]> veri, double[] regresyon, int xi) {
        System.out.println("Tahmin Edilen Degerler:");
        
        for (int i = 0; i < veri.size(); i++) {
            double tahmin = regresyon[0] + regresyon[1] * veri.get(i)[xi];
            System.out.printf("Gercek: %.4f, Tahmin: %.4f\n", veri.get(i)[5], tahmin);
        }
    }
}
