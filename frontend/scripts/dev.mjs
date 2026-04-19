#!/usr/bin/env node

import { spawn, spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';
import { existsSync, rmSync } from 'node:fs';

const __filename = fileURLToPath(import.meta.url);
const projectDir = dirname(dirname(__filename));
const lockPath = join(projectDir, '.next', 'dev', 'lock');

function listProcesses() {
  const result = spawnSync('ps', ['-eo', 'pid=,args='], {
    encoding: 'utf8',
  });

  if (result.error || result.status !== 0) {
    return [];
  }

  return result.stdout
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => {
      const firstSpace = line.indexOf(' ');
      if (firstSpace === -1) {
        return null;
      }
      const pid = Number(line.slice(0, firstSpace).trim());
      const cmd = line.slice(firstSpace + 1).trim();
      if (!Number.isInteger(pid) || !cmd) {
        return null;
      }
      return { pid, cmd };
    })
    .filter(Boolean);
}

function findRunningNextDevPids() {
  const marker = `${projectDir}/node_modules/.bin/next dev`;

  return listProcesses()
    .filter((proc) => proc.pid !== process.pid)
    .filter((proc) => proc.cmd.includes(marker))
    .map((proc) => proc.pid);
}

function killPid(pid) {
  try {
    process.kill(pid, 'SIGTERM');
  } catch {
    return;
  }
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function terminateExistingDevServers() {
  const pids = findRunningNextDevPids();
  if (pids.length === 0) {
    return;
  }

  console.log(`[dev] Stopping existing Next dev process(es): ${pids.join(', ')}`);

  for (const pid of pids) {
    killPid(pid);
  }

  const deadline = Date.now() + 4000;
  while (Date.now() < deadline) {
    const remaining = findRunningNextDevPids();
    if (remaining.length === 0) {
      return;
    }
    await sleep(150);
  }

  // Force kill if still alive after grace period.
  for (const pid of findRunningNextDevPids()) {
    try {
      process.kill(pid, 'SIGKILL');
    } catch {
      // ignore race conditions where the process exits between checks
    }
  }
}

function clearStaleLockFile() {
  if (!existsSync(lockPath)) {
    return;
  }

  try {
    rmSync(lockPath, { force: true });
    console.log(`[dev] Cleared stale lock file: ${lockPath}`);
  } catch (error) {
    console.warn(`[dev] Failed to remove lock file (${lockPath}): ${error.message}`);
  }
}

async function main() {
  await terminateExistingDevServers();
  clearStaleLockFile();

  const args = ['dev', ...process.argv.slice(2)];
  const child = spawn('next', args, {
    cwd: projectDir,
    stdio: 'inherit',
    shell: process.platform === 'win32',
  });

  child.on('exit', (code, signal) => {
    if (signal) {
      process.kill(process.pid, signal);
      return;
    }
    process.exit(code ?? 0);
  });

  child.on('error', (error) => {
    console.error(`[dev] Failed to start Next.js dev server: ${error.message}`);
    process.exit(1);
  });
}

main();
