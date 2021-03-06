package com.example.sadokmm.student.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.misc.ImageUtils;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.util.Constants;
import com.example.sadokmm.student.Activities.MainActivity;
import com.example.sadokmm.student.Objects.Emploi;
import com.example.sadokmm.student.Objects.Jour;
import com.example.sadokmm.student.Objects.Seance;
import com.example.sadokmm.student.Objects.User;
import com.example.sadokmm.student.R;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.example.sadokmm.student.Activities.firstActivity.EMPLOI_FILE;
import static com.example.sadokmm.student.Activities.firstActivity.SESSION;
import static com.example.sadokmm.student.Activities.firstActivity.admin;
import static com.example.sadokmm.student.Activities.firstActivity.getResizedBitmap;
import static com.example.sadokmm.student.Activities.firstActivity.listUser;
import static com.example.sadokmm.student.Activities.firstActivity.monEmploi;
import static com.example.sadokmm.student.Activities.firstActivity.publicUrl;

public class RegisterFragment extends Fragment {

    public final static int OPENCAM_CODE = 22;
    public final static int OPENGALLERY_CODE = 23;

    private Uri filepath;


    private TextView registerBtn,errorTextUsername,errorTextEmail,errorTextPass,errorTextPassConfirm,errorTextPhoto;
    private EditText email, pass, confirmPass,nom,prenom;
    private Bitmap myNewImage;
    private CircleImageView photo;
    private Spinner spinnerFiliere,spinnerNiveau,spinnerGroupe;
    private boolean ok;
    private String filieretxt;
    private int groupe,niveauFil;
    private String niveau;

    ArrayList<String> listFiliere = new ArrayList<>();
    ArrayList<String> listGroupe = new ArrayList<>();
    ArrayList<String> listNiveau = new ArrayList<>();

    ProgressDialog prgDialog;

    RequestQueue requestQueue;


    public RegisterFragment() {


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        nom=(EditText)view.findViewById(R.id.nom);
        prenom=(EditText)view.findViewById(R.id.prenom);
        registerBtn = (TextView) view.findViewById(R.id.registerBtn);
        email = (EditText) view.findViewById(R.id.emailRegister);
        pass = (EditText) view.findViewById(R.id.passRegister);
        confirmPass = (EditText) view.findViewById(R.id.confirmPassRegister);
        photo = (CircleImageView) view.findViewById(R.id.photoRegister);
        errorTextUsername = (TextView)view.findViewById(R.id.errorTextUsername);
        errorTextEmail = (TextView)view.findViewById(R.id.errorTextEmail);
        errorTextPass = (TextView)view.findViewById(R.id.errorTextPass);
        errorTextPassConfirm = (TextView)view.findViewById(R.id.errorTextPassConfirm);
        errorTextPhoto = (TextView)view.findViewById(R.id.errorTextPhoto);
        spinnerFiliere=(Spinner)view.findViewById(R.id.spinnerFiliere);
        spinnerGroupe=(Spinner)view.findViewById(R.id.spinnerGroupe);
        spinnerNiveau=(Spinner)view.findViewById(R.id.spinnerNiveau);


        remplirSpinnerNiveau();






        myNewImage=null;


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok=true;
                final String nomtxt,prenomtxt,passtxt,confrimPasstxt,emailtxt;
                nomtxt=nom.getText().toString();
                prenomtxt=prenom.getText().toString();
                passtxt=pass.getText().toString();
                confrimPasstxt=confirmPass.getText().toString();
                emailtxt=email.getText().toString();



                errorTextPassConfirm.setText("");
                errorTextPass.setText("");
                errorTextEmail.setText("");
                errorTextUsername.setText("");
                errorTextPhoto.setText("");

                if (nomtxt.isEmpty())
                {
                    errorTextUsername.setText("Veuillez saisir votre nom");
                    ok=false;
                }
                if (!isAlpha(nomtxt))
                {
                    errorTextUsername.setText("nom invalide");
                }
                if (prenomtxt.isEmpty())
                {
                    errorTextUsername.setText("Veuillez saisir votre prénom");
                    ok=false;
                }
                if (!isAlpha(prenomtxt))
                {
                    errorTextUsername.setText("prénom invalide");
                }
                if (emailtxt.isEmpty())
                {
                    errorTextEmail.setText("Veuillez saisir votre email");
                    ok=false;
                }
                if (!verifEmail(emailtxt))
                {
                    errorTextEmail.setText("Email invalide");
                    ok=false;
                }
                if (passtxt.isEmpty())
                {
                    errorTextPass.setText("Veuillez saisir votre mot de passe");
                    ok=false;
                }
                if (confrimPasstxt.isEmpty())
                {
                    errorTextPassConfirm.setText("Veuillez confirmer votre mot de passe");
                    ok=false;
                }
                if (myNewImage == null)
                {
                    errorTextPhoto.setText("Veuillez choisir votre photo");
                    ok=false;
                }
                if (!(passtxt.equals(confrimPasstxt)))
                {
                    errorTextPassConfirm.setText("Veuillez saisir le même mot de passe");
                    ok=false;
                }


                if (ok){

                    //listUser.add(new User("011",nomtxt,prenomtxt,emailtxt,passtxt,dToBitmap(myNewImage),filieretxt,groupe,niveauFil));

                    //Shared Preferences to save connectUser
                    SharedPreferences.Editor editor=getActivity().getSharedPreferences("userFile", MODE_PRIVATE).edit();
                    editor.putString("connectUser",emailtxt);
                    editor.commit();

                    try {
                        prgDialog = new ProgressDialog(getContext());
                        prgDialog.setMessage("Connexion en cours ...");
                        prgDialog.setIndeterminate(false);
                        //prgDialog.setMax(100);
                        prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        prgDialog.setCancelable(true);
                        prgDialog.show();
                        postUser(nomtxt,prenomtxt,emailtxt,passtxt,filieretxt,String.valueOf(niveauFil),String.valueOf(groupe));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    /**/

                }




            }
        });

        requestQueue = Volley.newRequestQueue(getContext());


        return view;


    }


    private void selectImage() {

        final String[] items = {"Camera", "Galerie"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choisir le source: ");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result=Utility.checkPermission(getContext());
                if (items[item].equals("Camera")) {
                    openCam();
                } else {
                    openGallery();

                }
            }
        });

        builder.show();
    }


    private void openCam() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, OPENCAM_CODE);

        }
    }


    private void openGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, OPENGALLERY_CODE);//one can be replaced with any action code
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case OPENCAM_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    myNewImage = getResizedBitmap((Bitmap)extras.get("data"),500);
                    photo.setImageBitmap(myNewImage);
                    //filepath = data.getData();
                }

                break;
            case OPENGALLERY_CODE:
                if (resultCode == RESULT_OK) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;

                    myNewImage = getResizedBitmap(ImageUtils.decodeStream(getActivity().getContentResolver(),data.getData(),options),500);
                    photo.setImageBitmap(myNewImage);
                    //filepath= data.getData();
                }
                break;
        }


    }



    boolean verifEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                if (!Character.isSpaceChar(c)) return false;

            }
        }

        return true;
    }





    public void remplirSpinnerNiveau(){

        listNiveau.add("1ère année");
        listNiveau.add("2ème année");
        listNiveau.add("3ème année");
        listNiveau.add("M1");
        listNiveau.add("M2");

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,listNiveau);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNiveau.setAdapter(arrayAdapter);

        spinnerNiveau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                niveau=adapterView.getItemAtPosition(i).toString();
                remplirListFiliere(niveau);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void remplirListFiliere(String niveau) {

        listFiliere.clear();

        if (niveau.equals("1ère année"))
        {
            listFiliere.add("LAG");
            listFiliere.add("LFE");
            listFiliere.add("LFIG");
            listFiliere.add("LFG");

            niveauFil=1;
        }

        if (niveau.equals("2ème année"))
        {
            listFiliere.add("LFE EQ");
            listFiliere.add("LFE FI");
            listFiliere.add("LFG");
            listFiliere.add("LFIG");
            listFiliere.add("LAC");
            listFiliere.add("LAF");
            listFiliere.add("LAIG");
            listFiliere.add("LA MGT");
            listFiliere.add("LA MKG");

            niveauFil=2;
        }

        if (niveau.equals("3ème année"))
        {
            listFiliere.add("LFC");
            listFiliere.add("LFE EQ");
            listFiliere.add("LFE FI");
            listFiliere.add("LFF");
            listFiliere.add("LFIG");
            listFiliere.add("LF MGT");
            listFiliere.add("LF MKG");
            listFiliere.add("LA MGT");
            listFiliere.add("LA MKG");
            listFiliere.add("LAC");
            listFiliere.add("LAF");
            listFiliere.add("LAIG");

            niveauFil=3;

        }

        if (niveau.equals("M1"))
        {
            listFiliere.add("M PRO GHT");
            listFiliere.add("M PRO ING FIN");
            listFiliere.add("M PRO MKG");
            listFiliere.add("M RECH COMPT");
            listFiliere.add("M RECH SIAD");

            niveauFil=1;
        }

        if (niveau.equals("M2"))
        {
            listFiliere.add("M PRO GHT");
            listFiliere.add("M PRO ING FIN");
            listFiliere.add("M PRO MKG");
            listFiliere.add("M PRO COMPT");
            listFiliere.add("M RECH COMPT");
            listFiliere.add("M RECH SIAD");
            listFiliere.add("M RECH FIN");
            listFiliere.add("M RECH ENT");

            niveauFil=2;

        }


        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,listFiliere);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiliere.setAdapter(arrayAdapter);


        spinnerFiliere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filieretxt=adapterView.getItemAtPosition(i).toString().toLowerCase();
                remplirListeGroupe();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    public void remplirListeGroupe() {

        listGroupe.clear();
        listGroupe.add("1");
        listGroupe.add("2");
        listGroupe.add("3");
        listGroupe.add("4");

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,listGroupe);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroupe.setAdapter(arrayAdapter);


        spinnerGroupe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                groupe=Integer.parseInt(adapterView.getItemAtPosition(i).toString().toLowerCase());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Bitmap p;


    }


    // try to save img to server

           public String getPath(Uri uri) {
                Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                cursor = getActivity().getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();

                return path;
            }



            public void postUser(String nom , String prenom , final String email , String pass , final String filiere , final String niveau , final String groupe) throws FileNotFoundException {
                SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, publicUrl+"student/u",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);
                                chargerMonEmploi(filiere,Integer.parseInt(niveau),Integer.parseInt(groupe),email);
                                Toast.makeText(getActivity(), "Okey", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                smr.addStringParam("nom", nom);
                smr.addStringParam("prenom", prenom);
                smr.addStringParam("email", email);
                smr.addStringParam("pass", pass);
                smr.addStringParam("filiere", filiere);
                smr.addStringParam("niveau", niveau);
                smr.addStringParam("groupe", groupe);
                smr.addFile("img", saveImage(myNewImage,"user"));

                RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
                mRequestQueue.add(smr);







            }



    public void chargerMonEmploi(String filiere , int niveau , int groupe , final String mail) {



        String url = publicUrl + "student/getemploi/"+filiere+"/"+niveau+"/"+groupe;
        Toast.makeText(getContext(),url,Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject myObject) {


                Toast.makeText(getContext(), "not null", Toast.LENGTH_LONG).show();

                try {


                    String id = myObject.getString("_id");
                    String maFiliere = myObject.getString("filiere");
                    int niveau = Integer.parseInt(myObject.getString("niveau"));
                    int groupe = Integer.parseInt(myObject.getString("groupe"));

                    monEmploi = new Emploi(id, maFiliere, niveau, groupe);

                    JSONArray joursArray = myObject.getJSONArray("jours");

                    String nomJour;

                    JSONArray seanceArray;
                    for (int i = 0; i < joursArray.length(); i++) {
                        nomJour = joursArray.getJSONObject(i).getString("nom");
                        Jour jj = new Jour(nomJour);
                        seanceArray = joursArray.getJSONObject(i).getJSONArray("seances");

                        for (int j = 0; j < seanceArray.length(); j++) {
                            JSONObject seance = seanceArray.getJSONObject(j);
                            String matiere = seance.getString("mat");
                            String enseignant = seance.getString("enseignant");
                            String salle = seance.getString("salle");
                            int numSeance = Integer.parseInt(seance.getString("numseance"));
                            String type = seance.getString("type");
                            String pqn= seance.getString("pq");
                            Boolean pq;
                            if (pqn.equals("false"))
                                pq = false ;
                            else pq=true;

                            if (!(matiere.equals(""))) {
                                Seance s = new Seance(matiere, enseignant, salle, type, numSeance, pq);
                                jj.getListSeance().add(s);
                            }

                        }
                        monEmploi.getJours().add(jj);

                    }

                    //Enregistrer une copie local de l'emploi sur le téléphone
                    Gson gson = new Gson();
                    String monEmploiEnJson = gson.toJson(monEmploi);

                    SharedPreferences.Editor editor=getContext().getSharedPreferences(EMPLOI_FILE,MODE_PRIVATE).edit();
                    editor.putString("emploi",monEmploiEnJson);
                    editor.commit();

                    chargerUser(mail);

                    //prgDialog.dismiss();



                    Toast.makeText(getContext(),monEmploiEnJson,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {

                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();

            }
        });

        requestQueue.add(jsonObjectRequest);

    }





    public void chargerUser(String emailusr ){

        String url = publicUrl + "student/getuser/"+emailusr;

        /*prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Connexion en cours ...");
        prgDialog.setIndeterminate(false);
        //prgDialog.setMax(100);
        prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prgDialog.setCancelable(false);
        prgDialog.show();*/

        Toast.makeText(getContext(),"chercheusr",Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                String id , email , nom , prenom , filiere  , imgUrl ;
                int niveau , groupe ;


                try {
                    id = jsonObject.getString("_id");
                    nom = jsonObject.getString("nom");
                    prenom = jsonObject.getString("prenom");
                    filiere = jsonObject.getString("filiere");
                    imgUrl = publicUrl + jsonObject.getString("img");
                    email = jsonObject.getString("email");
                    niveau = Integer.parseInt(jsonObject.getString("niveau"));
                    groupe = Integer.parseInt(jsonObject.getString("groupe"));
                    admin = new User(id, nom, prenom, email, imgUrl, filiere, groupe, niveau);

                    SharedPreferences.Editor editor=getContext().getSharedPreferences(SESSION,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("statut",true);
                    editor.putString("email",admin.getEmail());
                    editor.putString("prenom",admin.getPrenom());
                    editor.putString("nom" , admin.getNom());
                    editor.putString("filiere" , admin.getFiliere());
                    editor.putString("img",admin.getImg());
                    editor.putInt("groupe",admin.getGroupe());
                    editor.putInt("niveau",admin.getNiveau());
                    editor.commit();
                    Toast.makeText(getContext(),"chercheusr22",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("type","register");
                    intent.putExtra("email",email);
                    prgDialog.dismiss();
                    Toast.makeText(getContext(),"chercheusr33",Toast.LENGTH_LONG).show();
                    startActivity(intent);

                    Toast.makeText(getContext(),"chercheusr44",Toast.LENGTH_LONG).show();




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

        //return user;

    }


    private String saveImage(Bitmap bmp, String filename) throws FileNotFoundException {




        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Student";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "user-" + filename + ".png");
        FileOutputStream fOut = new FileOutputStream(file);

        bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String filePath = file.getPath();

        return filePath;

    }


}


