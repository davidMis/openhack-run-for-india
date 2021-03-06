package parser;

import android.util.Log;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDAO {
	private static String TAG = "XMLDAO";
    public static final String dateTimeFormat = "MM/dd/yyyy";

	private String[][] matx ;
	private int maxR=0;
	private int maxC =0;

    public int getMaxR(){
        return maxR;
    }
    public int getMaxC(){
        return maxC;
    }
	
	public XMLDAO(int row, int col){
		// defensive by alloting extra row and column
		maxR = row;
		maxC = col;
		matx = new String[row][col];
	}
	
	public void add( EntryDO entry){
		if(entry.getRow()>=0 && entry.getRow()<maxR && entry.getCol()>=0 && entry.getRow()<maxC && entry.getContent()!=null)
		matx[entry.getRow()][entry.getCol()] = entry.getContent();
	}

    public List<NotificationRow> getNotificationRows(){
        List<NotificationRow> rows = new ArrayList<NotificationRow>();
        for(int id=0;id<maxR;id++){
            NotificationRow row = new NotificationRow();
            for(int j=0;j<maxC;j++){
                if(this.matx[id][j] != null)
                row.addInfo(this.matx[id][j]);
            }
            if(!row.isEmpty()) {
                rows.add(row);
            }
        }

        return rows;
    }
	
	public void print(){
		for(int id=0;id<maxR;id++){
			for(int j=0;j<maxC;j++){
				Log.i(TAG, this.matx[id][j] + " " + id + j);
			}
		//	System.out.println();
		}
	}
	
	String temp = "";

    public String[][] getMatx() {
        return matx;
    }


     // send records only above current date
    public void readXML(String k, Calendar date) {
        readXML(k);
        String[][] rem = new String[maxC][maxR];
        try{
            for(int id=0, j=0;id<maxR;id++){

                if(datecompare(matx[id][0], date)){
                    rem[j]=matx[id];
                    j++;
                }else if (!matx[id][0].contains("/")){
                    rem[j]=matx[id];
                    j++;
                }
            }
            // reset the matrix
            matx = rem;
            maxR = rem.length;
        }catch(Exception e){
            System.out.println("unable to filter");
        }



    }

    public LinkedList<LinkedList<String>> returnList(){
        LinkedList<LinkedList<String>> li = new LinkedList<LinkedList<String>>();
        for(int i=0;i<this.maxR;i++){
            LinkedList<String> tt = new LinkedList<String>();
            for(int j=0;j<this.maxC;j++){
                if(this.matx[i][j]!=null)
                    tt.add(this.matx[i][j]);
            }
            li.add(tt);
        }
        return li;

    }

    private boolean datecompare(String string, Calendar date) {
        if(string == null)
            return false;
        System.out.println(string);

        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        Date date1 = null;
        try {
            date1 = sdf.parse(string);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
        String sDate = dateFormat.format(date.getTime());
        Date today = null;
        try {
            today = dateFormat.parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(date1 +""+today);

        if(date1.compareTo(today)>=0){
            return true;
        }
        return false;
    }

    public void readXML(String XML) {
		 
		    try {
		 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		 
			DefaultHandler handler = new DefaultHandler() {
		 
			boolean bfname = false;
			boolean blname = false;
			boolean bnname = false;
			boolean bsalary = false;
		 
			public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
		 
				//System.out.println("Start Element :" + qName);
		 
				if (qName.equalsIgnoreCase("title")) {
					bfname = true;
				}
		 
				if (qName.equalsIgnoreCase("content")) {
					blname = true;
				}
		 
				
		 
			}
		 
			public void endElement(String uri, String localName,
				String qName) throws SAXException {
		 
				//System.out.println("End Element :" + qName);
		 
			}
		 
			public void characters(char ch[], int start, int length) throws SAXException {
		 
				if (bfname) {
					String te = new String(ch, start, length);
				//	System.out.println("Title ***************: " + te);
					temp = te;
					bfname = false;
				}
		 
				if (blname) {
					String t2 = new String(ch, start, length);
				//	System.out.println("Content **************** : " + t2);
					EntryDO entry = new EntryDO();
					entry.setValue(temp, t2);
				//	System.out.println(entry.getRow());
				//	System.out.println(entry.getCol());
					matx[entry.getRow()][entry.getCol()] = entry.getContent();
					//reset
					temp = "";
					blname = false;
				}
		 
		 
			}
		 
		     };
		 

				DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			    InputSource is = new InputSource(new StringReader(XML));
			    saxParser.parse(is, handler);
		       //saxParser.parse("/Users/narendrabidari/Documents/workspace/FinalSemester/Assignment2Par/src/com/1.xml", handler);
		 
		     } catch (Exception e) {
		       e.printStackTrace();
		     }
		 
		   }

}
