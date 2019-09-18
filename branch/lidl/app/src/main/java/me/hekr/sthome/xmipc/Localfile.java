package me.hekr.sthome.xmipc;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/17.
 */

public class Localfile implements Serializable{

    private String filename;
    private String filepath;
    private String modifytime;
    private String thumbpath;


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }

    public String getThumbpath() {
        return thumbpath;
    }

    public void setThumbpath(String thumbpath) {
        this.thumbpath = thumbpath;
    }

    @Override
    public String toString() {
        return "Localfile{" +
                "filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", modifytime='" + modifytime + '\'' +
                ", thumbpath='" + thumbpath + '\'' +
                '}';
    }
}
