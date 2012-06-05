package org.kevoree.library.defaultNodeTypes.osgi.deploy.command

/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.kevoree._
import api.PrimitiveCommand
import api.service.core.handler.KevoreeModelHandlerService
import api.service.core.script.KevScriptEngineFactory
import framework.osgi.{KevoreeGroupActivator, KevoreeInstanceActivator, KevoreeInstanceFactory}
import framework.KevoreeGeneratorHelper
import library.defaultNodeTypes.jcl.deploy.context.KevoreeDeployManager
import library.defaultNodeTypes.osgi.deploy.{KevoreeOSGIMapping, OSGIKevoreeDeployManager}
import org.kevoree.framework.aspects.KevoreeAspects._
import org.slf4j.LoggerFactory

case class AddInstanceCommand(c: Instance, nodeName: String, modelservice: KevoreeModelHandlerService, kscript: KevScriptEngineFactory,bs : org.kevoree.api.Bootstraper) extends PrimitiveCommand {

  var logger = LoggerFactory.getLogger(this.getClass);
  var kevoreeFactory: KevoreeInstanceFactory = null
  var lastExecutionBundle: Option[org.osgi.framework.Bundle] = null

  def execute(): Boolean = {
    //FOUND CT SYMBOLIC NAME
    val mappingFound = KevoreeDeployManager.bundleMapping.find({
      bundle => bundle.name == c.getTypeDefinition.getName &&
        bundle.objClassName == c.getTypeDefinition.getClass.getName
    }) match {
      case Some(bundle) => bundle
      case None => {
        logger.error("Type Not Found: " + c.getTypeDefinition.getName)
        logger.error("mapping state=> " + KevoreeDeployManager.bundleMapping.toString())
      };
      return false;
      null;
    }

    logger.debug("bundleID for " + c.getTypeDefinition.getName + " =>" + mappingFound.asInstanceOf[KevoreeOSGIMapping].bundleID + "");

    val bundleType = OSGIKevoreeDeployManager.getBundleContext.getBundle(mappingFound.asInstanceOf[KevoreeOSGIMapping].bundleID)
    lastExecutionBundle = Some(bundleType)

    if (mappingFound != null) {
      //FOUND CURRENT NODE TYPE
      val nodeType = c.getTypeDefinition.eContainer.asInstanceOf[ContainerRoot].getNodes.find(tn => tn.getName == nodeName).get.getTypeDefinition
      //FIRST COMPLIANCE VALID TARGET NODE TYPE IN INHERITANCE
      val nodeTypeName = c.getTypeDefinition.foundRelevantHostNodeType(nodeType.asInstanceOf[NodeType], c.getTypeDefinition) match {
        case Some(nt) => nt.getName
        case None => throw new Exception("Can't  found compatible nodeType for this instance on this node type ")
      }

      val activatorPackage = KevoreeGeneratorHelper.getTypeDefinitionGeneratedPackage(c.getTypeDefinition, nodeTypeName)
      val factoryName = activatorPackage + "." + c.getTypeDefinition.getName + "Factory"
      try {

        kevoreeFactory = bundleType.loadClass(factoryName).newInstance().asInstanceOf[KevoreeInstanceFactory]
        val newInstance: KevoreeInstanceActivator = kevoreeFactory.registerInstance(c.getName, nodeName)
        KevoreeDeployManager.addMapping(new KevoreeOSGIMapping(c.getName, c.getClass.getName, newInstance, bundleType.getBundleId))


        newInstance.setKevScriptEngineFactory(kscript)
        newInstance.setModelHandlerService(modelservice)
        newInstance.start()
        if (newInstance.isInstanceOf[KevoreeGroupActivator]) {
          newInstance.asInstanceOf[KevoreeGroupActivator].groupActor.setBootStrapperService(bs)
        }

        //mustBeStarted = true
        true
      } catch {
        case _@e => {
          var message = "Could not start the instance " + c.getName + ":" + c.getClass.getName + " maybe because one of its dependencies is missing.\n"
          message += "Please check that all dependencies of your components are marked with a 'bundle' type (or 'provided' scope) in the pom of the component's project.\n"
          logger.error(message, e)
          false
        }
      }
    } else {
      false
    }
  }

  def undo() {
    lastExecutionBundle match {
      case None =>
      case Some(b) => {
        try {
          if (kevoreeFactory != null) {
            kevoreeFactory.remove(c.getName)
          }
          (KevoreeDeployManager.bundleMapping.filter(map => map.name == c.getName).toList ++ List()).foreach {
            map =>
              KevoreeDeployManager.removeMapping(map)
          }
        } catch {
          case _@e =>
        }
      }
    }
    /* TODO CALL refreshPackage */
  }
}
