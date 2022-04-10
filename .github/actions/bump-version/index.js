const fs = require('fs')
const proc = require('child_process')
const { EOL } = require('os')

const props = fs.readFileSync('gradle.properties', 'utf-8')
const lines = props.split('\n')
function getNewVersion(major, minor, patch) {
    let latestTag
    try {
        latestTag = proc.execSync('git describe --abbrev=0', { encoding: 'utf-8' })
    } catch (e) {
        latestTag = ''
    }

    let logs
    if (latestTag) {
        logs = proc.execSync(`git log ${latestTag}..HEAD --pretty=format:"%s"`, { encoding: 'utf-8' })
    } else {
        logs = proc.execSync('git log --pretty=format:"%s"', { encoding: 'utf-8' })
    }
    const logsLines = logs.split('\n')
    if (logsLines.some(l => l.startsWith('BREAKING CHANGE'))) {
        return `${major + 1}.${minor}.${patch}`
    }
    if (logsLines.some(l => l.startsWith('feat'))) {
        return `${major}.${minor + 1}.${patch}`
    }
    if (logsLines.some(l => l.startsWith('fix'))) {
        return `${major}.${minor}.${patch + 1}`
    }
    return `${major}.${minor}.${patch}`
}

for (let i = 0; i < lines.length; ++i) {
    const line = lines[i]
    if (line.startsWith('modVersion = ')) {
        const [, ver] = line.split('=').map(l => l.trim())
        const [major, minor, patch] = ver.split('.')
        const newVer = getNewVersion(Number(major), Number(minor), Number(patch))
        lines[i] = 'modVersion = ' + newVer
        break
    }
}

const [, ver] = lines.find(l => l.startsWith('modVersion = ')).split('=').map(l => l.trim())
const [, mc] = lines.find(l => l.startsWith('mcVersion = ')).split('=').map(l => l.trim())

process.stdout.write(EOL)
process.stdout.write(`::set-output name=version::${mc}-${ver}` + EOL)

fs.writeFileSync('gradle.properties', lines.join('\n'))
