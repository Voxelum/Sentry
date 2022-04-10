const fs = require('fs')
const { EOL } = require('os')

const props = fs.readFileSync('gradle.properties', 'utf-8')
const lines = props.split('\n')

const [, ver] = lines.find(l => l.startsWith('modVersion = ')).split('=').map(l => l.trim())
const [, mc] = lines.find(l => l.startsWith('mcVersion = ')).split('=').map(l => l.trim())

process.stdout.write(EOL)
process.stdout.write(`::set-output name=version::${mc}-${ver}` + EOL)
