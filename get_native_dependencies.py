#!/usr/bin/env python3
import struct
from socket import socket
from socketserver import BaseRequestHandler, ForkingTCPServer
from typing import Any, Tuple, Generator

from elftools.elf.dynamic import DynamicSegment
from elftools.elf.elffile import ELFFile


def get_dependencies(filename) -> Generator[str, Any, None]:
    with open(filename, 'rb') as f:
        elf = ELFFile(f)
        for seg in elf.iter_segments():
            if not isinstance(seg, DynamicSegment):
                continue
            for sym in seg.iter_tags():
                if sym['d_tag'] == 'DT_NEEDED':
                    yield sym.needed


def _recvn(s: socket, n: int) -> bytes:
    buf = bytearray(n)
    view = memoryview(buf)

    last_n = n
    while n:
        nbytes = s.recv_into(view, n)
        view = view[nbytes:]  # slicing views is cheap
        n -= nbytes
        if last_n != n or nbytes != 0:
            last_n = n

    return bytes(buf)


def _recvfmt(s: socket, fmt: str) -> Tuple[Any, ...]:
    st = struct.Struct(fmt)
    return st.unpack(_recvn(s, st.size))


MORE_DATA = 0xD


class DependencyCalculator(BaseRequestHandler):
    def handle(self):
        sck: socket = self.request
        length: int = _recvfmt(sck, '>i')[0]
        filename_bytes = _recvn(sck, length)
        filename = filename_bytes.decode('utf-8')
        for dep in get_dependencies(filename):
            dep_bytes = dep.encode('utf-8')
            sck.sendall(struct.pack('>bi', MORE_DATA, len(dep_bytes)) + dep_bytes)
        # Terminate request with NULL
        sck.sendall(b'\0')


def start_serving(port):
    with ForkingTCPServer(('localhost', port), DependencyCalculator) as server:
        print('Listening on', port, flush=True)
        server.allow_reuse_address = True
        server.serve_forever()


def main():
    from argparse import ArgumentParser

    parser = ArgumentParser()
    parser.add_argument('port', type=int, help='Port to listen on.')

    port: int = getattr(parser.parse_args(), 'port')

    start_serving(port)


if __name__ == '__main__':
    main()
