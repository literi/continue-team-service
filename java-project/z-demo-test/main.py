from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse
import os

# 创建 FastAPI 应用实例
app = FastAPI()

# 挂载静态文件目录：
# - "/static" 是访问静态文件的 URL 路径前缀
# - directory="static" 是本地存放静态文件的文件夹名称
# - name="static" 是挂载的名称（用于内部标识）
app.mount("/static", StaticFiles(directory="static"), name="static")

# 配置根路径 "/" 直接返回 index.html（可选，提升用户体验）
@app.get("/")
async def read_root():
    # 获取静态文件的绝对路径，避免路径问题
    html_path = os.path.join(os.path.dirname(__file__), "static", "index.html")
    return FileResponse(html_path)

# 如果你有多个 HTML 文件，也可以单独写接口返回
@app.get("/about")
async def read_about():
    about_path = os.path.join(os.path.dirname(__file__), "static", "about.html")
    return FileResponse(about_path)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)