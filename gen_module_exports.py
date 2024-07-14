import os


s = set()
for path, _, _ in os.walk('src'):
    if 'rarsreborn' in path:
        s.add(path[path.index('rarsreborn'):].replace('/', '.').replace('\\', '.'))

for e in s:
    print(f"exports {e};")
