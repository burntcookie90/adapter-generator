package io.dwak.adaptergenerator.processor.binding

import com.squareup.javapoet.*
import io.dwak.adaptergenerator.processor.model.ClassBinding
import java.io.IOException
import java.lang.reflect.Method
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class GeneratorBindingClass(val classPackage: String,
                            val className: String,
                            val targetClass: String,
                            val processingEnvironment: ProcessingEnvironment) {
  companion object {
    const val CLASS_SUFFIX = "Adapter"
  }

  private val targetClassName = ClassName.get(classPackage, targetClass)
  private val generatedClassName = ClassName.get(classPackage, className)
  private val messager: Messager by lazy { processingEnvironment.messager }
  private val elementUtils: Elements by lazy { processingEnvironment.elementUtils }
  private val typeUtils: Types by lazy { processingEnvironment.typeUtils }
  private val bindings = hashMapOf<String, ClassBinding>()

  fun createAndAddBinding(element: Element) {
    val binding = ClassBinding(element)
    bindings.put(binding.name, binding)
  }

  fun generate(): TypeSpec {
    val binding = bindings[targetClass]
    val recyclerAdapter = ClassName.get("android.support.v7.widget.RecyclerView", "Adapter")
    val viewHolder = ClassName.get(classPackage, targetClass)
    val fullAdapterTypeName = ParameterizedTypeName.get(recyclerAdapter, viewHolder)
    val modelType = TypeName.get(binding?.model)
    val listOfModel = ParameterizedTypeName.get(ClassName.get(List::class.java), modelType)
    val arrayListOfModel = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), modelType)

    val constructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("this(new \$T())", arrayListOfModel)
        .build()

    val secondaryConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(listOfModel, "list").build())
        .addStatement("this.list = new \$T(list)", arrayListOfModel)
        .build()

    val listOfModelField = FieldSpec.builder(listOfModel, "list")
        .addModifiers(Modifier.PRIVATE)
        .build()

    val layoutInflaterType = ClassName.get("android.view", "LayoutInflater")
    val viewType = ClassName.get("android.view", "View")
    val onCreateViewHolderMethod = MethodSpec.methodBuilder("onCreateViewHolder")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(ClassName.get("android.view", "ViewGroup"), "parent").build())
        .addParameter(ParameterSpec.builder(TypeName.INT, "viewType").build())
        .addAnnotation(AnnotationSpec.builder(Override::class.java).build())
        .addStatement("\$T view = \$T.from(parent.getContext()).inflate(\$L, parent, false)",
            viewType,
            layoutInflaterType,
            binding?.layoutResId)
        .addStatement("return new \$L(view)", binding?.name)
        .returns(targetClassName)
        .build()

    val clickListenerType = ClassName.get("android.view.View", "OnClickListener")
    val onClickAnonymous = TypeSpec.anonymousClassBuilder("")
        .superclass(clickListenerType)
        .addMethod(MethodSpec.methodBuilder("onClick")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(viewType, "view")
                .build())
            .beginControlFlow("if (listener != null)")
            .addStatement("listener.onItemClick(list.get(position))")
            .endControlFlow()
            .build())
        .build()

    val onBindViewHolderMethod = MethodSpec.methodBuilder("onBindViewHolder")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(targetClassName, "holder").build())
        .addParameter(ParameterSpec.builder(TypeName.INT, "position")
            .addModifiers(Modifier.FINAL)
            .build())
        .addAnnotation(AnnotationSpec.builder(Override::class.java).build())
        .addStatement("holder.itemView.setOnClickListener(\$L)", onClickAnonymous)
        .addStatement("holder.\$L(list.get(position))", binding?.bindMethod?.name)
        .build()

    val getItemCountMethod = MethodSpec.methodBuilder("getItemCount")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(AnnotationSpec.builder(Override::class.java).build())
        .addStatement("return list.size()")
        .returns(TypeName.INT)
        .build()

    val setItemsMethod = if (binding?.diffCallback == null) {
      MethodSpec.methodBuilder("setItems")
          .addModifiers(Modifier.PUBLIC)
          .addParameter(ParameterSpec.builder(listOfModel, "list").build())
          .addStatement("this.list = list")
          .addStatement("notifyDataSetChanged()")
          .build()
    }
    else {
      val diffUtil = ClassName.get("android.support.v7.util", "DiffUtil")
      val diffCallback = ClassName.get(binding.diffCallback)
      MethodSpec.methodBuilder("setItems")
          .addModifiers(Modifier.PUBLIC)
          .addParameter(ParameterSpec.builder(listOfModel, "list").build())
          .addStatement("\$T.calculateDiff(new \$T(this.list, list)).dispatchUpdatesTo(this)",
              diffUtil,
              diffCallback)
          .addStatement("this.list = new \$T(list)", arrayListOfModel)
          .build()
    }

    val getItemsMethod = MethodSpec.methodBuilder("getItems")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("return list")
        .returns(listOfModel)
        .build()

    val interactionListener = TypeSpec.interfaceBuilder("ItemClickListener")
        .addMethod(MethodSpec.methodBuilder("onItemClick")
            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(modelType, "model").build())
            .build())
        .build()


    val itemClickName = ClassName.get("$classPackage.$className", "ItemClickListener")
    val setOnClickMethod = MethodSpec.methodBuilder("setOnItemClickListener")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(itemClickName, "listener")
        .addStatement("this.listener = listener")
        .build()

    val clickField = FieldSpec.builder(itemClickName, "listener", Modifier.PRIVATE).build()

    return TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC)
        .addField(listOfModelField)
        .addField(clickField)
        .addMethod(constructor)
        .addMethod(secondaryConstructor)
        .superclass(fullAdapterTypeName)
        .addMethod(onCreateViewHolderMethod)
        .addMethod(onBindViewHolderMethod)
        .addMethod(getItemCountMethod)
        .addMethod(getItemsMethod)
        .addMethod(setItemsMethod)
        .addMethod(setOnClickMethod)
        .addType(interactionListener)
        .build()
  }

  @Throws(IOException::class)
  fun writeToFiler(filer: Filer) {
    JavaFile.builder(classPackage, generate()).build().writeTo(filer)
  }

}

