package wei.imageexplore.Table;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by wei on 2017/11/26.
 */

public class ImageInfo {
    public String path;
    public String h,w;
    public String size;
    public String date;
    private DecimalFormat format=new DecimalFormat("0.00");
    public String getDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(Long.parseLong(date));
        return format.format(d1);
    }

    public String getPixel(){
        return w+"x"+h;
    }

    public String getSize(){
        long size=Long.parseLong(this.size);
        if (size>1024*1024)
            return format.format(size/(1024.0*1024.0))+"M";
        return format.format(size/1024.0)+"K";
    }
}
