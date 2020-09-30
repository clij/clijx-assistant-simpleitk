package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
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

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.clijToITK;
import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.itkToCLIJ;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKCannyEdgeDetection")
public class SimpleITKCannyEdgeDetection extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number lower_threshold, Numer upper_threshold, Number variance, Number maximum_error";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, 0, 0, 0.0, 0.1};
    }

    @Override
    public boolean executeCL() {
        boolean result = simpleITKCannyEdgeDetection(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asFloat(args[2]), asFloat(args[3]), asFloat(args[4]), asFloat(args[5]));
        return result;
    }

    public static boolean simpleITKCannyEdgeDetection(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Float lower_threshold, Float upper_threshold, Float variance, Float maximum_error) {

        ClearCLBuffer inputFloat = input;
        // make sure that its type is float
        if (input.getNativeType() != clij2.Float) {
            inputFloat = clij2.create(input.getDimensions(), clij2.Float);
            clij2.copy(input, inputFloat);
        }

        // convert to ITK
        Image itk_input = clijToITK(clij2, inputFloat);

        if (input != inputFloat) {
            inputFloat.close();
        }

        // apply SimpleITK Median
        Image itk_output = SimpleITK.cannyEdgeDetection(itk_input, lower_threshold, upper_threshold,
                CLIJSimpleITKUtilities.packRadii(variance, variance, variance, (int)input.getDimension()),
                CLIJSimpleITKUtilities.packRadii(maximum_error, maximum_error, maximum_error, (int)input.getDimension()));

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
        return "Apply SimpleITKs Canny edge detection filter to an image.";
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
