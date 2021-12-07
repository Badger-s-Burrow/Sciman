package de.badgersburrow.sciman.objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import de.badgersburrow.sciman.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class Bib implements Serializable {

    private String Bibname;
    private String Comment;
    private String File;
    private boolean extern;
    private int ID;
    private String pdfDirectory;
    private String searchText;
    private int sortColumn;
    private boolean tableView;
    private boolean linked;
    private ArrayList<item> items = new ArrayList<item>();
    private String iconKey;
    private String iconBytes;
    private GregorianCalendar lastModified;
    private GregorianCalendar date;

    //extend class for remote sources
    private String source;//local or dropbox or manualApp
    private String manualApp;
    private String manualAppName;

    public Bib(String Bibname, String Source, String manualApp, String manualAppName, String Comment,
               boolean extern, String File, String PdfDirectory, String IconKey, String IconBytes, int ID) {
        this.Bibname = Bibname;
        this.Comment = Comment;
        this.File = File;
        this.extern = extern;
        this.ID = ID;
        this.pdfDirectory = PdfDirectory;
        this.iconKey = IconKey;
        this.iconBytes = IconBytes;
        this.searchText = "";
        this.tableView = false;
        this.sortColumn = 4;
        this.source = Source;
        this.linked = false;
        this.manualApp = manualApp;
        this.manualAppName = manualAppName;


        this.lastModified = new GregorianCalendar();
        lastModified.setTimeInMillis( this.getBibFile().lastModified() +1000);

        this.date = new GregorianCalendar();

    }


    //getter Methoden
    public String getBibname() {
        return Bibname;
    }

    public String getComment() {
        return Comment;
    }

    public boolean getExtern() {
        return extern;
    }

    public String getFile() {
        return File;
    }

    public String getPdfdirectory() {
        return pdfDirectory;
    }

    public String getIconKey() {
        return this.iconKey;
    }

    public int getID() {
        return ID;
    }

    public int getSortColumn() {
        return sortColumn;
    }

    public String getSearchText() {
        return searchText;
    }

    public boolean getTableView() {
        return tableView;
    }

    public String getSource() {
        return source;
    }

    public boolean getLinked() {
        return linked;
    }

    public String getManualApp() {
        return manualApp;
    }

    public String getManualAppName() {
        return manualAppName;
    }

    public ArrayList<item> getItems() {
        return items;
    }


    public Bitmap getIconBitmap() {

        byte[] decodedString = Base64.decode(this.iconBytes, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    //setter Methoden
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setSortColumn(int sortColumn) {
        this.sortColumn = sortColumn;
    }

    public void setTableView(boolean TableView) {
        this.tableView = TableView;
    }

    public void setLinked(boolean Linked) {
        this.linked = Linked;
    }

    public void setItems(ArrayList<item> Items) {
        this.items = Items;
    }


    //special methods for itemlist
    public void setItem(int numberOfItem, item Item) {
        this.items.set(numberOfItem, Item);
    }

    public item getItem(int numberOfItem) {
        return this.items.get(numberOfItem);
    }

    public void addItem(item Item) {
        this.items.add(Item);
    }

    public boolean readBibFile(Activity act) {
        try {
            File bibFile = getBibFile();
            //java.io.File f = new File(this.File);//"/tmp/diplomarbeit.bib");//Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OASVNlite/Bibliothek/diplomarbeit.bib");

            FileReader fr = new FileReader(bibFile);

            BufferedReader br = new BufferedReader(fr);
            String line = null;
            int i = 0;
            String startArticle = "@ARTICLE{";
            String startBook = "@BOOK{";
            String startInCollection = "@INCOLLECTION{";
            String startMasterThesis = "@MASTERTHESIS{";
            String startPhdThesis = "@PHDTHESIS{";
            String startThesis = "@THESIS{";
            String startProceedings = "@PROCEEDINGS{";
            String startInProceedings = "@INPROCEEDINGS{";
            String startMisc = "@MISC{";
            String startManual = "@MANUAL{";
            String startUnpublished = "@UNPUBLISHED{";
            String startOther = "@";
            String startComment = "@comment{";

            //am besten einmal vorher durchzuaehlen

            while ((line = br.readLine()) != null) {
                if ((line.startsWith(startArticle)) || (line.startsWith(startBook)) || (line.startsWith(startOther) && !line.startsWith(startComment))) {
                    String Type = act.getString(R.string.none);
                    String delete = "";
                    String lineedit1 = act.getString(R.string.none);
                    String Journal = act.getString(R.string.none);
                    String startJournal = "  journal";
                    if (line.toUpperCase(Locale.ENGLISH).startsWith(startArticle)) {
                        Type = "Article";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startBook)) {
                        Type = "Book";
                        startJournal = "  publisher";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startInCollection)) {
                        Type = "InCollection";
                        startJournal = "  publisher";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startMasterThesis)) {
                        Type = "MasterThesis";
                        startJournal = "  school";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startPhdThesis)) {
                        Type = "PhdThesis";
                        startJournal = "  school";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startThesis)) {
                        Type = "Thesis";
                        startJournal = "  institution";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startProceedings)) {
                        Type = "Proceedings";
                        startJournal = "  publisher";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startInProceedings)) {
                        Type = "InProceedings";
                        startJournal = "  publisher";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startMisc)) {
                        Type = "Misc";
                        //startJournal="  institution = {";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startManual)) {
                        Type = "Manual";
                        startJournal = "  organization";

                    } else if (line.toUpperCase(Locale.ENGLISH).startsWith(startUnpublished)) {
                        Type = "Unpublished";

                    } else {
                        Type = "Other";

                    }

                    String Bibtexkey = (line.split("\\{", 2)[1]).replace(",", delete);
                    String splitString = "= \\{";
                    String lastlineforitem = "}";

                    String closeField = "},";
                    String tsCloseField = "}";
                    String tabsubstitute = "\t";
                    String space = " ";

                    String startAuthor = "  author";
                    String Author = act.getString(R.string.none);

                    String startTitle = "  title";
                    String Title = act.getString(R.string.none);

                    String startYear = "  year";
                    String Year = act.getString(R.string.none);

                    String startAbstract = "  abstract";
                    String Abstract = act.getString(R.string.none);

                    String startDoi = "  doi";
                    String Doi = act.getString(R.string.none);

                    String startFile = "  file";
                    String File = act.getString(R.string.none);

                    String startTimestamp = "  timestamp";
                    String Timestamp = act.getString(R.string.none);

                    line = br.readLine();
                    while (!(line.startsWith(lastlineforitem))) {
                        //author:
                        if (line.toLowerCase(Locale.ENGLISH).startsWith(startAuthor)) {
                            if (!line.endsWith(closeField)) {
                                Author = line.replace(startAuthor, delete);
                                line = br.readLine();
                                while (!line.endsWith(closeField)) {
                                    Author += line;
                                    line = br.readLine();
                                }
                                Author += line.replace(closeField, delete);
                                Author = Author.replace(tabsubstitute, space);
                            } else {
                                Author = (line.split(splitString, 2))[1].replace(closeField, delete);
                            }

                        }
                        //title:
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startTitle)) {
                            if (!line.endsWith(closeField)) {
                                Title = line.replace(startTitle, delete);
                                line = br.readLine();
                                while (!line.endsWith(closeField)) {
                                    Title += line;
                                    line = br.readLine();
                                }
                                Title += line.replace(closeField, delete);
                                Title = Title.replace(tabsubstitute, space);
                            } else {
                                Title = (line.split(splitString, 2)[1]).replace(closeField, delete);
                            }

                        }
                        //year:
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startYear)) {
                            Year = (line.split(splitString, 2))[1].replace(closeField, delete) + space + space + space + space;

                        }
                        //volume
                        //number
                        //month
                        //pages
                        //doi
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startDoi)) {
                            Doi = (line.split(splitString, 2))[1].replace(closeField, delete);

                        }
                        //abstract:
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startAbstract)) {
                            if (!line.endsWith(closeField)) {
                                Abstract = line.replace(startAbstract, delete);
                                line = br.readLine();
                                while (!line.endsWith(closeField)) {
                                    Abstract += line;
                                    line = br.readLine();
                                }
                                Abstract += line.replace(closeField, delete);
                                Abstract = Abstract.replace(tabsubstitute, space);
                            } else {
                                Abstract = (line.split(splitString, 2))[1].replace(closeField, delete);
                            }

                        }
                        //file:
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startFile)) {
                            String[] extendedFiles = (line.split(splitString, 2)[1].replace(closeField, delete)).split(":");
                            for (String filename : extendedFiles) {
                                if (filename.length() > 4) {
                                    String extendedFile = filename;
                                    String[] splitFile = extendedFile.split("/");
                                    String nameFile = splitFile[splitFile.length - 1];

                                    File = this.pdfDirectory + "/" + nameFile;
                                    break;
                                }
                            }


                        }
                        //journal:
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startJournal)) {
                            Journal = line.split(splitString, 2)[1].replace(closeField, delete);
                        }
                        //journal:
                        else if (line.toLowerCase(Locale.ENGLISH).startsWith(startTimestamp)) {
                            Timestamp = line.split(splitString, 2)[1].replace(closeField, delete).replace(tsCloseField, delete);
                        }


                        line = br.readLine();
                    }

                    items.add(new item(Bibtexkey, Type, Author, Title, Year, Abstract, Journal, Doi, File, Timestamp, i));
                    i += 1;
                }
            }
            return true;

        } catch (IOException e) {
            Toast t = Toast.makeText(act.getApplicationContext(),
                    R.string.fnf, Toast.LENGTH_SHORT);
            return false;
        }
    }

    public File getBibFile(){
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String bibPath;
        File bibFile;

        if (this.getSource().equalsIgnoreCase("Dropbox")) {
            if (this.pdfDirectory.endsWith("/")) {
                bibPath = this.pdfDirectory + this.getFile();
            } else if (this.getFile().startsWith("/")) {
                bibPath = this.pdfDirectory + this.getFile();
            } else {
                bibPath = this.pdfDirectory + "/" + this.getFile();
            }
        } else {
            bibPath = this.getFile();
        }


        if (this.getExtern()) {

            if (bibPath.startsWith("/")) {
                bibPath = directory + bibPath;
                bibFile = new File(bibPath);

            } else {
                bibPath = directory + "/" + bibPath;
                bibFile = new File(bibPath);
            }
        }

        else

        {
            bibFile = new File(bibPath);
        }
        return bibFile;
    }

    public GregorianCalendar getLastModified(){return this.lastModified;}

    public String getPdfDirectory(){
        String pdfDirectoryTemp;
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (this.getExtern()){
            if (this.pdfDirectory.startsWith("/")){
                pdfDirectoryTemp = directory + pdfDirectory;


            }else{
                pdfDirectoryTemp = directory + "/" + pdfDirectory;

            }
            return pdfDirectoryTemp;
        }else{
            return this.pdfDirectory;
        }
    }

    public boolean isItemsOlder(){
        File bibFile = this.getBibFile();
        if (bibFile.exists()) {
            GregorianCalendar lastModified = new GregorianCalendar();
            lastModified.setTimeInMillis(bibFile.lastModified());
            if (lastModified.after(this.lastModified)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

}

