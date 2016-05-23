package course.examples.helloandroid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO Remove unused and unsafe constructors and set methods

public class Expiration {

    private int day = 1;
    private int month;
    private int year;
    private int nbExpiring = 1;
    private int nbTotal = 1;
    private String mExpCode = "";

    public Expiration(String expCode){

        if(expCode.length() >= 5 && expCode.length() < 7)
        {
            nbTotal = Integer.parseInt(expCode.substring(expCode.length()-2,expCode.length()));
            nbExpiring = Integer.parseInt(expCode.substring(expCode.length()-4,expCode.length()-2));
            month = Integer.parseInt(expCode.substring(0,expCode.length()-4));

            month = Integer.parseInt(expCode.substring(0,expCode.length()-4));

            findYear();

            mExpCode = expCode;
        }
    }


    public Expiration(int month){

        this.month = month;

        findYear();
    }

    public Expiration(int day, int month){
        Date date = new Date();

        this.day = day;
        this.month = month;

        year = Integer.parseInt(String.format("%tY", date));
    }

    public Expiration(int day, int month, int year){

        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Expiration(int month, int year, int nbExp, int nbTotal){

        this.month = month;
        this.year = year;
        this.nbExpiring = nbExp;
        this.nbTotal = nbTotal;
    }

    public Expiration(int day, int month, int year, int nbExp, int nbTotal){

        this.day = day;
        this.month = month;
        this.year = year;
        this.nbExpiring = nbExp;
        this.nbTotal = nbTotal;
    }

    public void setDate(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setNbExpiring(int nbExpiring){
        this.nbExpiring = nbExpiring;
    }

    public void setNbTotal(int nbTotal){
        this.nbTotal = nbTotal;
    }

    public String getExpCode() {
        return mExpCode;
    }

    public int getDay(){
        return day;
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

    public int getNbExpiring(){
        return nbExpiring;
    }

    public int getNbTotal(){
        return nbTotal;
    }

    public String getDateStr(){
        DateFormat df = new SimpleDateFormat("dd/MM/y");
        String dateInString = day + "/" + month  + "/" + year;
        Date date = null;
        try {
            date = df.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format("%tm", date) + "/" + String.format("%tY", date);
    }

    public Date getDate(){


        DateFormat df = new SimpleDateFormat("dd/MM/y");
        String dateInString = day + "/" + month  + "/" + year;
        Date date = null;
        try {
            date = df.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }


    public boolean isValid()
    {
        boolean isDateValid;
        boolean isNbExpValid;

        String dateInString = day + "/" + month  + "/" + year;

        try{
            DateFormat df = new SimpleDateFormat("dd/MM/y");
            df.setLenient(false);
            df.parse(dateInString);

            isDateValid = true;
        }catch(ParseException e){
            isDateValid =  false;
        }

        if((nbTotal >= nbExpiring) && (nbTotal*nbExpiring > 0))
            isNbExpValid = true;
        else
            isNbExpValid = false;

        return isDateValid && isNbExpValid;

    }

    private void findYear(){
        int currentMonth;

        Date date = new Date();
        currentMonth = Integer.parseInt(String.format("%tm", date));
        year = Integer.parseInt(String.format("%tY", date));

        if(month <= currentMonth){
            year++;
        }
    }

}
