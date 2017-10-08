/**
 * 
 */
package mate.is2.tools;

import mate.is2.data.Instances;
import mate.is2.data.ParametersFloat;
import mate.is2.tools.IPipe;
import mate.is2.util.OptionsSuper;

/**
 * @author Dr. Bernd Bohnet, 24.12.2010
 * 
 * 
 */
public interface Train {

	public abstract void writeModel(OptionsSuper options, IPipe pipe, ParametersFloat params);

	public abstract void readModel(OptionsSuper options);

	public abstract void train(OptionsSuper options, IPipe pipe, ParametersFloat params, Instances is);

	public abstract void out(OptionsSuper options, IPipe pipe, ParametersFloat params);

}