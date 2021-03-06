package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.itk.simple.Image;
import org.itk.simple.PixelIDValueEnum;
import org.itk.simple.SimpleITK;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKFFTConvolution")
public class SimpleITKFFTConvolution extends AbstractSimpleITKCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput {
    @Override
    public String getInputType() {
        return "Image";
    }

    @Override
    public String getOutputType() {
        return "Image";
    }

    @Override
    public String getParameterHelpText() {
        return "Image input, Image input_kernel, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKFFTConvolution(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), (ClearCLBuffer) (args[2])));
        return result;
    }

    public static synchronized boolean simpleITKFFTConvolution(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer input_kernel, ClearCLBuffer output) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);
        Image itk_input_kernel = clijToITK(clij2, input_kernel);
        itk_input = SimpleITK.cast(itk_input, PixelIDValueEnum.sitkFloat32);
        itk_input_kernel = SimpleITK.cast(itk_input_kernel, PixelIDValueEnum.sitkFloat32);

        // apply SimpleITK FFT convolution
        Image itk_output = SimpleITK.fFTConvolution(itk_input, itk_input_kernel);

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
        return "Convolve an image with a kernel image using SimpleITK and a fast Fourier transform (FFT).\n\n" +
                "See also: https://simpleitk.org/doxygen/latest/html/classitk_1_1simple_1_1FFTConvolutionImageFilter.html";
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
