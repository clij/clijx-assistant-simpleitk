package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.itk.simple.Image;
import org.itk.simple.PixelIDValueEnum;
import org.itk.simple.SimpleITK;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKYenThreshold")
public class SimpleITKYenThreshold extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKYenThreshold(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1])));
        return result;
    }

    public static synchronized boolean simpleITKYenThreshold(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);

        // apply Simple ITK Yen Threshold
        Image itk_output = SimpleITK.yenThreshold(itk_input);

        // push result back
        ClearCLBuffer result = itkToCLIJ(clij2, itk_output);

        // save it in the right place
        clij2.binaryNot(result, output);

        // clean up
        result.close();

        return true;
    }


    @Override
    public String getDescription() {
        return "Apply SimpleITKs Yen Threshold to an image.\n\n" +
                "Compared to SimpleITKs implementation, results are inverted to match ImageJs behaviour.\n" +
                "See also: https://simpleitk.org/doxygen/latest/html/classitk_1_1simple_1_1YenThresholdImageFilter.html";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Binary,Segmentation";
    }
}
