package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.itk.simple.Image;
import org.itk.simple.SimpleITK;
import org.itk.simple.VectorUInt32;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKMedian")
public class SimpleITKMedian extends AbstractSimpleITKCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number radius_x, Number radius_y, Number radius_z";
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKMedian(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asFloat(args[2]), asFloat(args[3]), asFloat(args[4])));
        return result;
    }

    public static synchronized boolean simpleITKMedian(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Float radius_x, Float radius_y, Float radius_z) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);

        // apply SimpleITK Median
        Image itk_output = SimpleITK.median(itk_input, CLIJSimpleITKUtilities.packRadii(radius_x.intValue(), radius_y.intValue(), radius_z.intValue(), (int)input.getDimension()));

        // push result back
        ClearCLBuffer result = itkToCLIJ(clij2, itk_output);

        // save it in the right place
        clij2.copy(result, output);

        // clean up
        result.close();

        return true;
    }


    @Override
    public String getDescription() {
        return "Apply SimpleITKs Median filter to an image.\n\n" +
                "See also: https://simpleitk.org/doxygen/latest/html/classitk_1_1simple_1_1MedianImageFilter.html";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Filter";
    }
}
