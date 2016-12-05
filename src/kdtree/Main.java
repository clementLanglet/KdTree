package kdtree;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main 
{
	public static void main(String[] args)
    {
        System.out.println("Entrer le nom de l'image à charger :");
        String filename = new Scanner(System.in).nextLine();
        
        try{
            File pathToFile = new File(filename);
            BufferedImage img = ImageIO.read(pathToFile);

            int imgHeight = img.getHeight();
            int imgWidth  = img.getWidth();
            BufferedImage res_img = new BufferedImage(imgWidth, imgHeight, img.getType());
            BufferedImage id_img = new BufferedImage(imgWidth, imgHeight, img.getType());

/////////////////////////////////////////////////////////////////
//TODO: replace this naive image copy by the quantization
/////////////////////////////////////////////////////////////////
/*            for (int y = 0; y < imgHeight; y++) {
                for (int x = 0; x < imgWidth; x++) {
                    int Color = img.getRGB(x,y);
                    int R = (Color >> 16) & 0xff;
                    int G = (Color >> 8) & 0xff;
                    int B = Color & 0xff;

                    int resR = R, resG = G, resB = B;

                    int cRes = 0xff000000 | (resR << 16)
                                          | (resG << 8)
                                          | resB;
                    res_img.setRGB(x,y,cRes);
                }
            }*/
/////////////////////////////////////////////////////////////////
// Quantization
/////////////////////////////////////////////////////////////////
            ArrayList<Point3i> tab_pixel = new ArrayList<Point3i>();
            for (int y = 0; y < imgHeight; y++){
            	for (int x = 0; x < imgWidth; x++) {
            		int Color = img.getRGB(x, y);
                    int R = (Color >> 16) & 0xff;
                    int G = (Color >> 8) & 0xff;
                    int B = Color & 0xff;
                    
                    Point3i p = new Point3i(R, G, B);
            		tab_pixel.add(p);
            	}
            }
      
            
            KdTree tree = new KdTree(3);
            tree.buildTree(tab_pixel, 0, 8); //256 couleurs
            ArrayList<Point3i> palette_colors = new ArrayList<Point3i>();
            tree.getPointsFromLeaf(palette_colors);
            
            
            
/////////////////////////////////////////////////////////////////            

            ImageIO.write(id_img, "jpg", new File("ResId.jpg"));
            ImageIO.write(res_img, "jpg", new File("ResColor.jpg"));
/////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
