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
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKHMaxima")
public class SimpleITKHMaxima extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number h";
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKHMaxima(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asFloat(args[2])));
        return result;
    }

    public static synchronized boolean simpleITKHMaxima(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Float h) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);

        // apply SimpleITK HMaxima
        Image itk_output = SimpleITK.hMaxima(itk_input, h);

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
        return "Apply SimpleITKs H-Maxima filter to an image.\n\n" +
                "Suppress local maxima whose height above the baseline is less than h." +
                "See also: https://simpleitk.org/doxygen/latest/html/classitk_1_1simple_1_1HMaximaImageFilter.html";
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
