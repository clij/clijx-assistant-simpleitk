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
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKConvolution")
public class SimpleITKConvolution extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, Image input_kernel, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = simpleITKConvolution(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), (ClearCLBuffer) (args[2]));
        return result;
    }

    public static synchronized boolean simpleITKConvolution(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer input_kernel, ClearCLBuffer output) {

        ClearCLBuffer input_float = convertFloat(clij2, input);
        ClearCLBuffer input_kernel_float = convertFloat(clij2, input_kernel);

        // convert to ITK
        Image itk_input = clijToITK(clij2, input_float);
        Image itk_input_kernel = clijToITK(clij2, input_kernel_float);

        if (input_float != input) {
            input_float.close();
        }
        if (input_kernel_float != input) {
            input_kernel_float.close();
        }

        // apply SimpleITK convolution
        Image itk_output = SimpleITK.convolution(itk_input, itk_input_kernel);

        // push result back
        ClearCLBuffer result = itkToCLIJ(clij2, itk_output);

        // save it in the right place
        clij2.copy(result, output);

        // clean up
        result.close();

        return true;
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.Float);
    }

    @Override
    public String getDescription() {
        return "Convolve an image with a kernel image using SimpleITK.";
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
