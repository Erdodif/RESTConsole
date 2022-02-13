package hu.petrik;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static String url = "https://retoolapi.dev/Q304E6/film";
    private static int menuPoint = 0;

    public static void main(String[] args) {
        boolean fine = false;
        while (!fine) {
            try {
                outAllFilms(url);
                fine = true;
            } catch (MalformedURLException e) {
                System.out.println("Az elérési út megváltozott, vagy hibás!");
                setNewUrl();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Nem várt hiba történt: " + e.getMessage());
                setNewUrl();
            }
        }
        menuPoint = menu();
        while (menuPoint != 0){
            switch (menuPoint){
                case 1:
                    try {
                        outAllFilms(url);
                    }catch (Exception e){
                        System.err.print("Sikertelen művelet "+e.getMessage());
                    }
                    break;
                case 2:
                    Film film = filmByInput();
                    try {
                        addFilm(url,film);
                    } catch (IOException e) {
                        System.err.print("Sikertelen művelet "+e.getMessage());
                    }
                    break;
                default:
                    System.err.print("Nem deffiniált menüpont!");
                    break;
            }
            menuPoint = menu();
        }

    }

    static private int menu(){
        System.out.println("**Menü**\n1 > Filmek listázása\n2 > Új film hozzáadása\n0 > kilépés");
        Integer choice = null;
        Scanner sc = new Scanner(System.in);
        while (choice == null){
            try {
                System.out.print("> ");
                choice = sc.nextInt();
                if (choice < 0){
                    throw new IllegalArgumentException("Negatív menüpont nem lehetséges!");
                }
                if (choice > 2){
                    throw new IllegalArgumentException("2-nél nagyobb menüpont nem lehetséges!");
                }
            }
            catch (Exception e){
                System.err.print("Hiba "+e.getMessage());
            }
        }
        return choice;
    }

    static private void setNewUrl() {
        System.out.println("Állíts be új elérési utat!");
        System.out.println(url);
        Scanner scan = new Scanner(System.in);
        String editable = scan.nextLine();
        System.out.println(editable);
        url = scan.nextLine();
    }

    private static void outAllFilms(String url) throws IOException {
        URL urlobject = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlobject.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sor = br.readLine();
        while (sor != null) {
            System.out.println(sor);
            sor = br.readLine();
        }
    }

    private static Film filmByInput(){
        String cim = "";
        String kategoria = "";
        int hossz = 0;
        int ertekeles = 0;
        int done = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Új fim hozzáadása...");
        while (done <4){
            try {
                switch (done){
                    case 0:
                        System.out.print("Adja meg a film címét: ");
                        cim = sc.nextLine();
                        break;
                    case 1:
                        System.out.print("Adja meg a film kategóriáját: ");
                        kategoria = sc.nextLine();
                        break;
                    case 2:
                        System.out.print("Adja meg a film hosszát (egész): ");
                        hossz = Integer.parseInt(sc.nextLine());
                        break;
                    case 3:
                        System.out.print("Adja meg a film értékelését (1-10): ");
                        ertekeles = Integer.parseInt(sc.nextLine());
                        break;
                }
                done++;
            }
            catch (Exception e){
                System.err.println("Hibás adat!\tújra...");
            }
        }
        return new Film(0,cim,kategoria,hossz,ertekeles);
    }

    private static void addFilm(String url, Film film) throws IOException {
        URL urlobject = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlobject.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        bw.write(film.toJson());
        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sor = br.readLine();
        while (sor != null) {
            System.out.println(sor);
            sor = br.readLine();
        }
    }
}
