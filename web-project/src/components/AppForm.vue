<template>
  <div class="app-form">
    <el-form :inline="inline" :model="formData" :label-position="labelPosition" :label-width="labelWidth" v-bind="$attrs">
      <el-form-item
        v-for="field in fields"
        :key="field.prop"
        :label="field.label"
        :prop="field.prop"
        :rules="field.rules"
      >
        <!-- 输入框 -->
        <el-input
          v-if="field.type === 'input' || !field.type"
          v-model="formData[field.prop]"
          :placeholder="field.placeholder || `请输入${field.label}`"
          :clearable="field.clearable !== false"
          :disabled="field.disabled"
          :readonly="field.readonly"
          :maxlength="field.maxlength"
          @change="handleChange(field.prop, $event)"
          @keyup.enter="handleEnter"
        />

        <!-- 文本域 -->
        <el-input
          v-else-if="field.type === 'textarea'"
          v-model="formData[field.prop]"
          type="textarea"
          :placeholder="field.placeholder || `请输入${field.label}`"
          :clearable="field.clearable !== false"
          :disabled="field.disabled"
          :readonly="field.readonly"
          :maxlength="field.maxlength"
          :rows="field.rows || 4"
          @change="handleChange(field.prop, $event)"
        />

        <!-- 下拉选择 -->
        <el-select
          v-else-if="field.type === 'select'"
          v-model="formData[field.prop]"
          :placeholder="field.placeholder || `请选择${field.label}`"
          :clearable="field.clearable !== false"
          :disabled="field.disabled"
          :multiple="field.multiple"
          :filterable="field.filterable"
          style="min-width: 150px;"
          @change="handleChange(field.prop, $event)"
        >
          <el-option
            v-for="option in field.options"
            :key="field.optionValue ? option[field.optionValue] : option.value"
            :label="field.optionLabel ? option[field.optionLabel] : option.label"
            :value="field.optionValue ? option[field.optionValue] : option.value"
          />
        </el-select>

        <!-- 日期选择 -->
        <el-date-picker
          v-else-if="field.type === 'date'"
          v-model="formData[field.prop]"
          type="date"
          :placeholder="field.placeholder || `选择${field.label}`"
          :disabled="field.disabled"
          :format="field.format || 'YYYY-MM-DD'"
          :value-format="field.valueFormat || 'YYYY-MM-DD'"
          style="min-width: 150px;"
          @change="handleChange(field.prop, $event)"
        />

        <!-- 日期范围选择 -->
        <el-date-picker
          v-else-if="field.type === 'daterange'"
          v-model="formData[field.prop]"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :disabled="field.disabled"
          :format="field.format || 'YYYY-MM-DD'"
          :value-format="field.valueFormat || 'YYYY-MM-DD'"
          style="min-width: 250px;"
          @change="handleChange(field.prop, $event)"
        />

        <!-- 数字输入 -->
        <el-input-number
          v-else-if="field.type === 'number'"
          v-model="formData[field.prop]"
          :placeholder="field.placeholder || `请输入${field.label}`"
          :disabled="field.disabled"
          :min="field.min"
          :max="field.max"
          :step="field.step"
          style="width: 100%;"
          @change="handleChange(field.prop, $event)"
        />

        <!-- 开关 -->
        <el-switch
          v-else-if="field.type === 'switch'"
          v-model="formData[field.prop]"
          :disabled="field.disabled"
          :active-text="field.activeText"
          :inactive-text="field.inactiveText"
          @change="handleChange(field.prop, $event)"
        />

        <!-- 单选 -->
        <el-radio-group
          v-else-if="field.type === 'radio'"
          v-model="formData[field.prop]"
          :disabled="field.disabled"
          @change="handleChange(field.prop, $event)"
        >
          <el-radio
            v-for="option in field.options"
            :key="field.optionValue ? option[field.optionValue] : option.value"
            :label="field.optionValue ? option[field.optionValue] : option.value"
          >
            {{ field.optionLabel ? option[field.optionLabel] : option.label }}
          </el-radio>
        </el-radio-group>

        <!-- 多选 -->
        <el-checkbox-group
          v-else-if="field.type === 'checkbox'"
          v-model="formData[field.prop]"
          :disabled="field.disabled"
          @change="handleChange(field.prop, $event)"
        >
          <el-checkbox
            v-for="option in field.options"
            :key="field.optionValue ? option[field.optionValue] : option.value"
            :label="field.optionValue ? option[field.optionValue] : option.value"
          >
            {{ field.optionLabel ? option[field.optionLabel] : option.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <!-- 表单操作按钮 -->
      <el-form-item v-if="showActions" class="form-actions">
        <slot name="actions">
          <el-button v-if="showSubmit" type="primary" @click="handleSubmit" :loading="submitting">
            {{ submitButtonText }}
          </el-button>
          <el-button v-if="showReset" @click="handleReset">
            {{ resetButtonText }}
          </el-button>
          <slot name="extra-actions"></slot>
        </slot>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, PropType } from 'vue'
import { ElMessage } from 'element-plus'

// 字段类型定义
type FieldType = 
  | 'input' 
  | 'textarea' 
  | 'select' 
  | 'date' 
  | 'daterange' 
  | 'number' 
  | 'switch' 
  | 'radio' 
  | 'checkbox'

// 表单字段配置
interface FormField {
  prop: string
  label: string
  type?: FieldType
  placeholder?: string
  clearable?: boolean
  disabled?: boolean
  readonly?: boolean
  maxlength?: number
  rows?: number
  multiple?: boolean
  filterable?: boolean
  min?: number
  max?: number
  step?: number
  activeText?: string
  inactiveText?: string
  options?: Array<{
    label: string
    value: any
    [key: string]: any
  }>
  optionLabel?: string
  optionValue?: string
  format?: string
  valueFormat?: string
  rules?: any[]
  [key: string]: any
}

// 定义 props
const props = defineProps({
  fields: {
    type: Array as PropType<FormField[]>,
    required: true,
    default: () => []
  },
  modelValue: {
    type: Object,
    default: () => ({})
  },
  inline: {
    type: Boolean,
    default: true
  },
  labelPosition: {
    type: String as PropType<'left' | 'right' | 'top'>,
    default: 'right'
  },
  labelWidth: {
    type: String,
    default: 'auto'
  },
  showActions: {
    type: Boolean,
    default: false
  },
  showSubmit: {
    type: Boolean,
    default: true
  },
  showReset: {
    type: Boolean,
    default: true
  },
  submitButtonText: {
    type: String,
    default: '提交'
  },
  resetButtonText: {
    type: String,
    default: '重置'
  },
  validateBeforeSubmit: {
    type: Boolean,
    default: true
  }
})

// 定义 emits
const emit = defineEmits([
  'update:modelValue',
  'submit',
  'reset',
  'change',
  'field-change'
])

// 响应式数据
const formData = ref<Record<string, any>>({})
const submitting = ref(false)

// 初始化表单数据
const initializeFormData = () => {
  const initialData: Record<string, any> = {}
  
  props.fields.forEach(field => {
    // 如果 modelValue 中有对应值，则使用该值，否则使用默认值
    if (props.modelValue && props.modelValue[field.prop] !== undefined) {
      initialData[field.prop] = props.modelValue[field.prop]
    } else {
      // 根据字段类型设置默认值
      switch (field.type) {
        case 'checkbox':
          initialData[field.prop] = field.defaultValue || []
          break
        case 'switch':
          initialData[field.prop] = field.defaultValue || false
          break
        case 'number':
          initialData[field.prop] = field.defaultValue || 0
          break
        case 'select':
          if (field.multiple) {
            initialData[field.prop] = field.defaultValue || []
          } else {
            initialData[field.prop] = field.defaultValue || ''
          }
          break
        default:
          initialData[field.prop] = field.defaultValue || ''
      }
    }
  })
  
  formData.value = initialData
}

// 监听 modelValue 变化
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    // 只更新存在的字段，避免覆盖用户操作
    Object.keys(newValue).forEach(key => {
      if (formData.value.hasOwnProperty(key)) {
        formData.value[key] = newValue[key]
      }
    })
  }
}, { deep: true })

// 监听 formData 变化并同步到父组件
watch(formData, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })

// 初始化表单数据
initializeFormData()

// 处理字段值变化
const handleChange = (field: string, value: any) => {
  formData.value[field] = value
  emit('field-change', field, value, formData.value)
  emit('change', formData.value)
}

// 处理回车键
const handleEnter = () => {
  if (props.showSubmit) {
    handleSubmit()
  }
}

// 处理提交
const handleSubmit = async () => {
  if (props.validateBeforeSubmit) {
    // 这里可以添加表单验证逻辑
    // 由于 Element Plus 的表单验证需要特定的 ref，我们简化处理
  }
  
  submitting.value = true
  try {
    emit('submit', formData.value)
  } catch (error) {
    console.error('Form submission error:', error)
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 处理重置
const handleReset = () => {
  initializeFormData()
  emit('reset', formData.value)
}

// 暴露方法给父组件
defineExpose({
  getFormData: () => formData.value,
  setFormData: (data: Record<string, any>) => {
    Object.assign(formData.value, data)
  },
  reset: handleReset,
  submit: handleSubmit
})
</script>

<style lang="scss" scoped>
.app-form {
  .form-actions {
    margin-bottom: 0;
  }
  
  .el-form-item {
    margin-bottom: 18px;
    
    &:last-child {
      margin-right: 0;
    }
  }
}
</style>