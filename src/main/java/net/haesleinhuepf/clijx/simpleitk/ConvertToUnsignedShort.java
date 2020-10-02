package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_convertToUnsignedShort")
public class ConvertToUnsignedShort extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = convertToUnsignedShort(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]));
        return result;
    }

    public static synchronized boolean convertToUnsignedShort(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output) {
        if (output.getNativeType() != NativeTypeEnum.UnsignedShort) {
            System.out.println("Warning (convertToUnsignedShort): output has wrong type: " + output.getNativeType());
        }

        clij2.copy(input, output);
        return true;
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.UnsignedShort);
    }

    @Override
    public String getDescription() {
        return "Convert image to 16-bit unsigned short pixel type.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }
}
