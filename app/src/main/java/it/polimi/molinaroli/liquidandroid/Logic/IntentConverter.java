package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.Intent;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import xdroid.toaster.Toaster;

/**
 * class to convert intent in a JSON object an vice versa
 */

public class IntentConverter {

    final static String ACTION = "action";
    final static String EXTRAS = "extras";
    //PRIMITIVI
    final static String BOOLEAN = "boolean";
    final static String BYTE = "byte";
    final static String CHARSEQUENCE = "charsequence";
    final static String DOUBLE = "double";
    final static String FLOAT = "float";
    final static String INTEGER = "integer";
    final static String LONG = "long";
    final static String SHORT = "short";
    final static String STRING = "string";
    //VETTORI
    final static String ABOOLEAN = "aboolean";
    final static String ABYTE = "abyte";
    final static String ACHARSEQUENCE = "acharsequence";
    final static String ADOUBLE = "adouble";
    final static String AFLOAT = "afloat";
    final static String AINTEGER = "ainteger";
    final static String ALONG = "along";
    final static String ASHORT = "ashort";
    final static String ASTRING = "astring";
    //ARRAYLIST
    final static String ALINTEGER = "alinteger";
    final static String ALSTRING = "alstring";
    final static String ALCHARSEQUENCE = "alcharsequence";
    //COSTRUZIONE DEL JSON
    final static String TYPE = "type";
    final static String DATA = "data";
    final static String KEY = "key";


    /**
     * convert an intent into a JSON to be sent on the socket
     * @param i
     * @return
     */
    public static JSONObject intentToJSON(Intent i){
        JSONObject obj = new JSONObject();
        try {

            obj.put(ACTION,i.getAction());
            JSONArray extr = new JSONArray();
            Bundle b = i.getExtras();
            Set<String> keys = b.keySet();
            for(String key : keys){
                Object cur = b.get(key);

                JSONObject j = new JSONObject();
                //INIZIO PRIMITIVI
                if (cur instanceof Integer){
                    j.put(TYPE,INTEGER);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof String){
                    j.put(TYPE,STRING);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof Boolean ){
                    j.put(TYPE,BOOLEAN);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof Byte){
                    j.put(TYPE,BYTE);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof Double){
                    j.put(TYPE,DOUBLE);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof Float){
                    j.put(TYPE,FLOAT);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof CharSequence){
                    j.put(TYPE,CHARSEQUENCE);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof Long){
                    j.put(TYPE,LONG);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                }
                else if (cur instanceof Short){
                    j.put(TYPE,SHORT);
                    j.put(DATA,cur);
                    j.put(KEY,key);
                } //INIZIO VETTORI
                else if (cur instanceof int[]){
                    int[] v = (int[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,INTEGER);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,AINTEGER);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof String[]){
                    String[] v = (String[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,STRING);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ASTRING);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof boolean[] ){
                    boolean[] v = (boolean[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,BOOLEAN);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ABOOLEAN);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof byte[]){
                    byte[] v = (byte[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,BYTE);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ABYTE);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof double[]){
                    double[] v = (double[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,DOUBLE);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ADOUBLE);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof float[]){
                    float[] v = (float[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,FLOAT);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,AFLOAT);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof CharSequence[]){
                    CharSequence[] v = (CharSequence[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,CHARSEQUENCE);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ACHARSEQUENCE);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof long[]){
                    long[] v = (long[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,LONG);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ALONG);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }
                else if (cur instanceof short[]){
                    short[] v = (short[]) cur;
                    JSONArray ar = new JSONArray();
                    for(int k = 0;k<v.length;k++){
                        JSONObject job = new JSONObject();
                        job.put(TYPE,SHORT);
                        job.put(DATA,v[k]);
                        ar.put(job);
                    }
                    j.put(TYPE,ASHORT);
                    j.put(DATA,ar);
                    j.put(KEY,key);
                }//INIZIO ARRAYLIST
                else if (cur instanceof ArrayList<?>){
                    Object o = ((ArrayList<?>)cur).get(0);
                    if (o instanceof String){
                        ArrayList<String> al = (ArrayList<String>) cur;
                        JSONArray ar = new JSONArray();
                        for(String s : al){
                            JSONObject job = new JSONObject();
                            job.put(TYPE,STRING);
                            job.put(DATA,s);
                            ar.put(job);
                        }
                        j.put(TYPE,ALSTRING);
                        j.put(DATA,ar);
                        j.put(KEY,key);
                    }else if (o instanceof Integer){
                        ArrayList<Integer> al = (ArrayList<Integer>) cur;
                        JSONArray ar = new JSONArray();
                        for(Integer s : al){
                            JSONObject job = new JSONObject();
                            job.put(TYPE,INTEGER);
                            job.put(DATA,s);
                            ar.put(job);
                        }
                        j.put(TYPE,ALINTEGER);
                        j.put(DATA,ar);
                        j.put(KEY,key);
                    }else if (o instanceof  CharSequence){
                        ArrayList<CharSequence> al = (ArrayList<CharSequence>) cur;
                        JSONArray ar = new JSONArray();
                        for(CharSequence s : al){
                            JSONObject job = new JSONObject();
                            job.put(TYPE,CHARSEQUENCE);
                            job.put(DATA,s);
                            ar.put(job);
                        }
                        j.put(TYPE,ALCHARSEQUENCE);
                        j.put(DATA,ar);
                        j.put(KEY,key);
                    }

                }//ho analizzato tutti i tipi possibili e costruito l'oggetto
                extr.put(j); //da controllare se funziona
            }
            obj.put(EXTRAS,extr);
        } catch (JSONException e) {
            Toaster.toast("error convertin intent to json");
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * converts a JSONObject into a regular android intent
     * @param j
     * @return
     */
    public static Intent JSONToIntent(JSONObject j){
        Intent intent = new Intent();
        try {
            intent.setAction(j.getString(ACTION)); //parsing action
            JSONArray extras = j.getJSONArray(EXTRAS);
            for(int k = 0;k<extras.length();k++){
                JSONObject job = extras.getJSONObject(k);
                String tipo;
                tipo = job.getString(TYPE);
                switch (tipo){
                    case BOOLEAN:
                        boolean d = job.getBoolean(DATA);
                        intent.putExtra(job.getString(KEY),d);
                        break;
                    case BYTE:
                        byte b = (byte) job.get(DATA);
                        intent.putExtra(job.getString(KEY),b);
                        break;
                    case CHARSEQUENCE:
                        CharSequence c = (CharSequence) job.get(DATA);
                        intent.putExtra(job.getString(KEY),c);
                        break;
                    case DOUBLE:
                        double db = job.getDouble(DATA);
                        intent.putExtra(job.getString(KEY),db);
                        break;
                    case FLOAT:
                        float f = (float) job.get(DATA);
                        intent.putExtra(job.getString(KEY),f);
                        break;
                    case INTEGER:
                        int i = job.getInt(DATA);
                        intent.putExtra(job.getString(KEY),i);
                        break;
                    case LONG:
                        long l = job.getLong(DATA);
                        intent.putExtra(job.getString(KEY),l);
                        break;
                    case SHORT:
                        short sh = (short) job.get(DATA);
                        intent.putExtra(job.getString(KEY),sh);
                        break;
                    case STRING:
                        String st = job.getString(DATA);
                        intent.putExtra(job.getString(KEY),st);
                        break;
                    //VETTORI
                    case ABOOLEAN: break;
                    case ABYTE: break;
                    case ACHARSEQUENCE: break;
                    case ADOUBLE: break;
                    case AFLOAT: break;
                    case AINTEGER: break;
                    case ALONG: break;
                    case ASHORT: break;
                    case ASTRING: break;
                    //ARRAYLIST
                    case ALINTEGER: break;
                    case ALSTRING: break;
                    case ALCHARSEQUENCE: break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Converter","conversion failed");
        }
        return intent;
    }
}