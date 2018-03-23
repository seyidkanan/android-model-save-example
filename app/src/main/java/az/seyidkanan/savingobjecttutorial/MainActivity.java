package az.seyidkanan.savingobjecttutorial;

import android.content.Context;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;

import az.seyidkanan.savingobjecttutorial.model.UserModel;

public class MainActivity extends AppCompatActivity {

    private static final String DB_NAME = "TEST_ENV";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
    }

    public void saveWithJSON(View view) {
        Gson gson = new Gson();
        UserModel userModel = new UserModel();
        userModel.setName("Name");
        userModel.setSurname("Surname");
        String json = gson.toJson(userModel);
        prefsEditor.putString("user_model_json", json);
        prefsEditor.commit();
        Log.e("kanan", "data wrote json");
        readJson();

    }

    private void readJson() {
        Log.e("kanan", "data read json");
        Gson gson = new Gson();
        String gsonString = sharedPreferences.getString("user_model_json", "");
        UserModel obj = gson.fromJson(gsonString, UserModel.class);
        Log.e("kanan", obj.toString());
    }

    public <T extends Serializable> T stringToObject(String data) {
        byte[] bytes = Base64.decode(data, 0);
        T object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (T) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public String objectToString(UserModel object) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            encoded = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public void saveWithSerializable(View view) {
        Log.e("kanan", "data write sr");
        UserModel userModel = new UserModel();
        userModel.setName("Name");
        userModel.setSurname("Surname");
        prefsEditor.putString("user_model_sr", objectToString(userModel));
        prefsEditor.apply();

        Log.e("kanan", "data read sr");

        String data = sharedPreferences.getString("user_model_sr", "");

        UserModel userModel1 = stringToObject(data);
        Log.e("kanan", userModel1.toString());
    }

    public void saveWithCoreModel(View view) {
        UserModel userModel = new UserModel();
        userModel.setName("Name");
        userModel.setSurname("Surname");

        Log.e("kanan", "data write with core model");

        setObject(userModel);

        Log.e("kanan", "data read with core model");

        UserModel model = getObject();

        Log.e("kanan", model.toString());

    }

    private UserModel getObject() {
        UserModel o = new UserModel();
        Field[] fields = o.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                try {
                    final String name = field.getName();
                    if (type == Character.TYPE || type.equals(String.class)) {
                        field.set(o, sharedPreferences.getString(name, ""));
                    } else if (type.equals(int.class) || type.equals(Short.class))
                        field.setInt(o, sharedPreferences.getInt(name, 0));
                    else if (type.equals(double.class))
                        field.setDouble(o, sharedPreferences.getFloat(name, 0));
                    else if (type.equals(float.class))
                        field.setFloat(o, sharedPreferences.getFloat(name, 0));
                    else if (type.equals(long.class))
                        field.setLong(o, sharedPreferences.getLong(name, 0));
                    else if (type.equals(Boolean.class))
                        field.setBoolean(o, sharedPreferences.getBoolean(name, false));
                    else if (type.equals(UUID.class))
                        field.set(
                                o,
                                UUID.fromString(
                                        sharedPreferences.getString(
                                                name,
                                                UUID.nameUUIDFromBytes("".getBytes()).toString()
                                        )
                                )
                        );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    private void setObject(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            try {
                final String name = field.getName();
                if (type == Character.TYPE || type.equals(String.class)) {
                    Object value = field.get(o);
                    if (value != null)
                        prefsEditor.putString(name, value.toString());
                } else if (type.equals(int.class) || type.equals(Short.class))
                    prefsEditor.putInt(name, field.getInt(o));
                else if (type.equals(double.class))
                    prefsEditor.putFloat(name, (float) field.getDouble(o));
                else if (type.equals(float.class))
                    prefsEditor.putFloat(name, field.getFloat(o));
                else if (type.equals(long.class))
                    prefsEditor.putLong(name, field.getLong(o));
                else if (type.equals(Boolean.class))
                    prefsEditor.putBoolean(name, field.getBoolean(o));
                else if (type.equals(UUID.class))
                    prefsEditor.putString(name, field.get(o).toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        prefsEditor.apply();
    }

}
