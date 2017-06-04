package mindtrack.muslimorganizer.utility;

/**
 * Class to log application events in txt file
 */
public class MindtrackLog {

    /**
     * Function to add new MindtrackLog
     *
     * @param content Content text
     */
    public static void add(String content) {
        /*try {

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Muslim_organizer_log.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            bw.write(currentDateandTime + " : " + content + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
