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

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.clijToITK;
import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.itkToCLIJ;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKOtsuMultipleThresholds")
public class SimpleITKOtsuMultipleThresholds extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number number_of_thresholds";
    }

    @Override
    public boolean executeCL() {
        boolean result = simpleITKOtsuMultipleThresholds(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asInteger(args[2]));
        return result;
    }

    public static synchronized boolean simpleITKOtsuMultipleThresholds(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Integer number_of_thresholds) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);

        // apply Simple ITK Otsu Multiple Thresholds
        Image itk_output = SimpleITK.otsuMultipleThresholds(itk_input, number_of_thresholds.shortValue());

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
        return "Apply SimpleITKs Otsu Multiple Thresholds to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Segmentation";
    }
}
