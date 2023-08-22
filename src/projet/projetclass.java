package projet;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import com.filenet.api.admin.StorageArea;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.StorageAreaSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
public class projetclass {
    private static final double NULL = 0;

	public static void main(String[] args) throws SQLException, IOException, ParseException {
        // Set connection parameters; substitute for the placeholders.
        String uri = "http://192.168.56.101:9080/wsi/FNCEWS40MTOM/";
        String username = "GCD Administrator";
        String password = "P@ssw0rd";

        // Make connection.
        Connection conn = Factory.Connection.getConnection(uri);
        Subject subject = UserContext.createSubject(conn, username, password, null);
        UserContext.get().pushSubject(subject);

        // Connect to the database.
        String jdbcUrl = "jdbc:mysql://localhost:3306/demo";
        String dbUsername = "root";
        String dbPassword = "";
        java.sql.Connection dbConn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

        // Insert data into the database.
        String req = "INSERT INTO serveur (`Nom_Serveur`, `IP`, `Createur`, `Zone_de_Stockage`, `Taille_Max_Zone`, `Taille_utilise`, `Nombre_Fichiers`, `Max_Files`, `Memoire_totale`,`Memoire_utilise` ,`disque_totale`, `disque_utilise`,`status`) VALUES (?,?, ?, ?, ?, ?, ?, ? ,?,?,?,?,?)";
        PreparedStatement stat = dbConn.prepareStatement(req);

        try {
            // Get default domain.
            Domain domain = Factory.Domain.fetchInstance(conn, null, null);
            System.out.println("Domain: " + domain.get_Name());

            // Get object stores for domain.
            ObjectStoreSet osSet = domain.get_ObjectStores();
            Iterator<ObjectStore> osIter = osSet.iterator();

            while (osIter.hasNext()) {
                ObjectStore store = osIter.next();
                StorageAreaSet storageAreas = store.get_StorageAreas();
                Iterator<StorageArea> saIter = storageAreas.iterator();

                while (saIter.hasNext()) {
                    StorageArea sa = saIter.next();
                    
                    if (!sa.get_DisplayName().equals("Default Database Storage Area")) {
                        String x = store.get_Name();
                        stat.setString(1, x);

                        String connURI = conn.getURI();
                        String regexPattern = "http://([\\d\\.]+)(:\\d+)?/.*";
                        Pattern pattern = Pattern.compile(regexPattern);
                        Matcher matcher = pattern.matcher(connURI);
                        if (matcher.find()) {
                            connURI = matcher.group(1);
                        }
                        // limit connURI to maximum 50 characters
                        if (connURI.length() > 50) {
                            connURI = connURI.substring(0, 50);
                        }
                        stat.setString(2, connURI);
                        
                        stat.setString(3, store.get_Creator());
                        
                        

                        stat.setString(4, sa.get_DisplayName());
                        
                        
                        Double maxSizeKBytes = sa.get_MaximumSizeKBytes();
                        if (maxSizeKBytes != null) {
                            stat.setDouble(5, maxSizeKBytes);
                        } else {
                            stat.setDouble(5, 0);
                        }

                        Double contentSizeKBytes = sa.get_ContentElementKBytes();
                        if (contentSizeKBytes != null) {
                            Locale locale = Locale.getDefault(); // or specify a different locale if needed
                            DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
                            decimalFormat.applyPattern("#.#"); // one decimal place
                            double roundedContentSize = decimalFormat.parse(decimalFormat.format(contentSizeKBytes)).doubleValue();
                            stat.setDouble(6, roundedContentSize);
                        } else {
                            stat.setDouble(6, 0);
                        }
                        
                        stat.setFloat(7, sa.get_ContentElementCount().floatValue());
                        
                        Double maxNumElements = sa.get_MaximumContentElements();
                        
                        stat.setFloat(8, sa.get_ContentElementCount().floatValue());
                        
                        File disk = new File("C:"); 
                        long totalCapacity = disk.getTotalSpace(); // total capacity in bytes
                        long freeSpace = disk.getFreeSpace(); // free space in bytes
                        
                        // convert bytes to GB (gigabytes)
                        double totalCapacityGB = (double) totalCapacity / (1024 * 1024 * 1024);
                        double freeSpaceGB = (double) freeSpace / (1024 * 1024 * 1024);
                        double usedSpaceGB = totalCapacityGB - freeSpaceGB;

                        // calculate percentage values                   
                        double usedSpacePercentage = (double) usedSpaceGB / totalCapacityGB * 100;
                        System.out.printf("Total capacity: %.2f GB\n", totalCapacityGB);                   
                        System.out.printf("Used space: %.2f%%\n", usedSpacePercentage);

                        // set values to the database
                        stat.setDouble(9, totalCapacityGB); 
                        stat.setDouble(10, usedSpacePercentage);
                    

                    
                    //VM
                    File disk1 = new File("C:"); 
                    long totalCapacity1 = disk1.getTotalSpace(); // total capacity in bytes
                    long freeSpace1 = disk1.getFreeSpace(); // free space in bytes
                    
                    // convert bytes to GB (gigabytes)
                    double totalCapacityGB1 = (double) totalCapacity1 / (1024 * 1024 * 1024);
                    double freeSpaceGB1 = (double) freeSpace1 / (1024 * 1024 * 1024);
                    double usedSpaceGB1 = totalCapacityGB1 - freeSpaceGB1;

                    // calculate percentage values                   
                    double usedSpacePercentage1 = (double) usedSpaceGB1 / totalCapacityGB1 * 100;
                    System.out.printf("Total capacity: %.2f GB\n", totalCapacityGB1);                   
                    System.out.printf("Used space: %.2f%%\n", usedSpacePercentage1);
                    // set values to the database  
                    
                    stat.setDouble(11, totalCapacityGB1); 
                    stat.setDouble(12, usedSpacePercentage1);     
                    stat.setString(13, "");
                    
                    
                    stat.executeUpdate();             
                }
            }
            }
                System.out.println("Connection to Content Platform Engine successful");
            } finally {
                UserContext.get().popSubject();
            
            }
        }
    }