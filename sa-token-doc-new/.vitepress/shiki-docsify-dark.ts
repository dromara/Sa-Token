/**
 * Shiki 高亮主题：对齐旧站 Docsify 的 Prism 配色（暗底 #191919）。
 * 给 config.ts markdown.theme 用。改颜色就改下面 tokenColors 的 foreground。
 *
 * Java 观感：类名青 #55b5db、方法/函数橙 #e96900、数字棕橙、字符串绿 #42b983。
 */
import type { ThemeRegistration } from 'shiki'
const docsifyDark: ThemeRegistration = {
  name: 'sa-token-docsify-dark',
  type: 'dark',
  bg: '#191919',
  fg: '#FFFFFF',
  colors: {
    'editor.background': '#191919',
    'editor.foreground': '#FFFFFF'
  },
  tokenColors: [
    { settings: { foreground: '#FFFFFF' } },

    { scope: 'comment', settings: { foreground: '#8e908c' } },
    { scope: 'comment.block.html', settings: { foreground: '#CDAB53' } },

    { scope: 'string', settings: { foreground: '#42b983' } },
    { scope: ['string.quoted.double.html', 'string.quoted.single.html'], settings: { foreground: '#E6DB74' } },
    { scope: ['string.quoted.double.xml', 'string.quoted.single.xml'], settings: { foreground: '#A6E22E' } },
    { scope: ['string.quoted.js', 'string.quoted.double.js', 'string.quoted.single.js', 'string.template.js'], settings: { foreground: '#dddddd' } },

    { scope: ['constant.numeric', 'constant.language', 'constant.character'], settings: { foreground: '#c76b29' } },

    { scope: 'keyword', settings: { foreground: '#db2d20' } },
    { scope: 'keyword.operator', settings: { foreground: '#dddddd' } },
    { scope: 'storage.modifier', settings: { foreground: '#db2d20' } },
    { scope: ['storage.modifier.import', 'storage.modifier.package'], settings: { foreground: '#01A252' } },

    { scope: ['storage.type.java', 'storage.type.annotation.java'], settings: { foreground: '#55b5db' } },
    { scope: 'storage.type.primitive.java', settings: { foreground: '#db2d20' } },
    { scope: ['storage.type.js', 'storage.type.function.js', 'storage.type.ts'], settings: { foreground: '#e96900' } },

    { scope: 'entity.name.function', settings: { foreground: '#e96900' } },
    { scope: ['entity.name.class', 'entity.name.type', 'entity.name.type.class'], settings: { foreground: '#55b5db' } },
    { scope: ['support.class', 'support.type'], settings: { foreground: '#55b5db' } },
    { scope: 'support.function', settings: { foreground: '#e96900' } },
    { scope: 'variable.other.object', settings: { foreground: '#55b5db' } },
    { scope: 'variable.parameter', settings: { foreground: '#3d8fd1' } },

    { scope: 'punctuation', settings: { foreground: '#dddddd' } },
    { scope: 'punctuation.definition.comment', settings: { foreground: '#8e908c' } },
    { scope: 'punctuation.definition.string', settings: { foreground: '#42b983' } },
    { scope: 'punctuation.definition.string.html', settings: { foreground: '#E6DB74' } },
    { scope: 'punctuation.definition.string.js', settings: { foreground: '#dddddd' } },

    // XML/HTML：标签名也要红。Shiki 实际 scope 是 entity.name.tag.localname.xml，
    // 写 entity.name.tag.xml 对不上，标签名会掉成白色，只剩尖括号是红的。
    { scope: ['entity.name.tag', 'punctuation.definition.tag'], settings: { foreground: '#db2d20' } },
    { scope: 'entity.other.attribute-name', settings: { foreground: '#A6E22E' } },
    { scope: 'entity.name.tag.yaml', settings: { foreground: '#e96900' } },
    { scope: 'punctuation.separator.key-value.mapping.yaml', settings: { foreground: '#eeeeee' } },

    { scope: 'keyword.other.definition.ini', settings: { foreground: '#22a2c9' } },

    { scope: 'source.js', settings: { foreground: '#01A252' } },
    { scope: ['source.js keyword', 'source.js storage'], settings: { foreground: '#e96900' } },
    { scope: 'source.js punctuation', settings: { foreground: '#dddddd' } },
    { scope: 'source.shell', settings: { foreground: '#01A252' } }
  ]
}

export default docsifyDark
