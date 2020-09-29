package net.haesleinhuepf.clijx.simpleitk;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import org.itk.simple.Image;
import org.itk.simple.SimpleITK;

import java.io.File;

public class CLIJSimpleITKUtilities {

    static final String format = "tif";

    public static Image clijToITK(CLIJ2 clij2, ClearCLBuffer input) {
        String filename = IJ.getDirectory("temp") + "/clijitk" + System.currentTimeMillis() + "." + format;
        clij2.saveAsTIF(input, filename);
        //ImagePlus imp = clij2.pull(input);
        //IJ.run(imp, "Nrrd ... ", "nrrd=" + filename);


        Image image = SimpleITK.readImage(filename);
        new File(filename).delete();
        return image;
    }


    public static ClearCLBuffer itkToCLIJ(CLIJ2 clij2, Image input) {
        String filename = IJ.getDirectory("temp") + "/clijitk" + System.currentTimeMillis() + "."  + format;
        SimpleITK.writeImage(input, filename);
        ImagePlus imp = IJ.openImage(filename);
        new File(filename).delete();
        return clij2.push(imp);
    }
}
