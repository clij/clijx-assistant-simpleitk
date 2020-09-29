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

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.clijToITK;
import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.itkToCLIJ;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKDanielssonDistanceMap")
public class SimpleITKDanielssonDistanceMap extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = simpleITKDanielssonDistanceMap(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]));
        return result;
    }

    public static synchronized boolean simpleITKDanielssonDistanceMap(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output) {

        ClearCLBuffer inverted = clij2.create(input);
        clij2.binaryNot(input, inverted);

        // convert to ITK
        Image itk_input = clijToITK(clij2, inverted);
        inverted.close();

        // apply SimpleITK distance map
        Image itk_output = SimpleITK.danielssonDistanceMap(itk_input, true);

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
        return "Generate SimpleITKs Danielsson distance map from a binary image.\n\n" +
                "Compared to SimpleITK, the image is binary inverted before the map is generated to make the operation similar to ImageJs implementation.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Binary,Filter";
    }
}
