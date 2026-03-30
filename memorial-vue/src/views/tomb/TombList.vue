<template>
  <div>
    <div class="page-wrap">
      <el-card class="left-card">
        <div class="left-toolbar">
          <span class="left-title">家族树</span>
        </div>
        <el-tree
          v-loading="treeLoading"
          :data="familyTreeData"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          highlight-current
          default-expand-all
          @node-click="handleTreeNodeClick"
        />
      </el-card>

      <el-card class="right-card">
        <div class="toolbar" :class="{ 'toolbar-mobile': isMobile }">
          <!-- 手机端：第一行 选择家族+新增墓碑，第二行 搜索框+搜索 -->
          <template v-if="isMobile">
            <div class="toolbar-row toolbar-row-1">
              <el-button class="tree-toggle-btn" @click="treeDrawerVisible = true">
                <el-icon><List /></el-icon>选择家族
              </el-button>
              <el-button v-if="canAddTomb" type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增墓碑</el-button>
            </div>
            <div class="toolbar-row toolbar-row-2 toolbar-row-search">
              <el-input v-model="query.keyword" placeholder="搜索逝者姓名" clearable class="search-input" @clear="loadData" />
              <el-button type="primary" class="search-btn" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
            </div>
          </template>
          <!-- 桌面端：原有布局 -->
          <template v-else>
            <div class="toolbar-left toolbar-left--search">
              <el-input v-model="query.keyword" placeholder="搜索逝者姓名" clearable class="search-input" @clear="loadData" />
              <el-button type="primary" class="search-btn" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
            </div>
            <el-button v-if="canAddTomb" type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增墓碑</el-button>
          </template>
        </div>

        <div class="current-family">
          {{ currentFamilyTitle }}
          <el-button type="primary" link size="small" @click="treeDrawerVisible = true" class="change-family-link">切换</el-button>
        </div>

        <!-- 桌面端：表格 -->
        <div class="tomb-desktop">
          <el-table :data="tableData" border stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column label="照片" width="80">
              <template #default="{ row }">
                <el-avatar :size="48" :src="row.photo" shape="square" v-if="row.photo" />
                <el-avatar :size="48" shape="square" v-else><el-icon><Picture /></el-icon></el-avatar>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="逝者姓名" />
            <el-table-column label="出生日期" min-width="160">
              <template #default="{ row }"><span v-html="formatTombBirth(row)"></span></template>
            </el-table-column>
            <el-table-column label="逝世日期" min-width="160">
              <template #default="{ row }"><span v-html="formatTombDeath(row)"></span></template>
            </el-table-column>
            <el-table-column prop="familyName" label="所属家族" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.familyName" type="success">{{ row.familyName }}</el-tag>
                <span v-else class="text-muted">未关联</span>
              </template>
            </el-table-column>
            <el-table-column label="二维码" width="120" align="center" class-name="col-qr-preview">
              <template #default="{ row }">
                <div class="qr-mem-col">
                  <el-button size="small" type="primary" link @click="openQrDialog(row)">下载</el-button>
                  <el-button size="small" type="info" link @click="goMemorialPage(row)">页面预览</el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="visitCount" label="访问量" width="90" />
            <el-table-column prop="messageCount" label="留言数" width="90" />
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button v-if="canOperate(row)" size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
                <el-button v-if="canOperate(row)" size="small" type="warning" link @click="openStoryDrawer(row)">事迹</el-button>
                <el-button size="small" type="success" link @click="viewDetail(row)">详情</el-button>
                <el-button
                  size="small"
                  link
                  :class="row.myReminderOn ? 'reminder-on' : 'reminder-off'"
                  @click="openListReminderDialog(row)"
                >
                  提醒
                </el-button>
                <el-button v-if="canOperate(row)" size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 手机端：卡片列表 -->
        <div class="tomb-mobile">
          <div v-loading="loading" class="tomb-card-list">
            <div v-for="row in tableData" :key="row.id" class="tomb-card">
              <div class="tomb-card__main">
                <el-avatar :size="56" :src="row.photo" shape="square" class="tomb-card__photo">
                  <el-icon :size="28"><Picture /></el-icon>
                </el-avatar>
                <div class="tomb-card__info">
                  <div class="tomb-card__name">{{ row.name }}</div>
                  <div class="tomb-card__meta">
                    <span v-html="formatTombBirth(row)"></span>
                    <span class="tomb-card-meta-sep"> — </span>
                    <span v-html="formatTombDeath(row)"></span>
                  </div>
                  <div class="tomb-card__family">
                    <el-tag v-if="row.familyName" type="success" size="small">{{ row.familyName }}</el-tag>
                    <span v-else class="text-muted">未关联</span>
                  </div>
                  <div class="tomb-card__stats">访问 {{ row.visitCount ?? 0 }} · 留言 {{ row.messageCount ?? 0 }}</div>
                </div>
              </div>
              <div class="tomb-card__qr-mem">
                <el-button size="small" type="primary" link @click="openQrDialog(row)">下载</el-button>
                <el-button size="small" type="info" link @click="goMemorialPage(row)">页面预览</el-button>
              </div>
              <div class="tomb-card__actions">
                <el-button v-if="canOperate(row)" size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
                <el-button v-if="canOperate(row)" size="small" type="warning" link @click="openStoryDrawer(row)">事迹</el-button>
                <el-button size="small" type="success" link @click="viewDetail(row)">详情</el-button>
                <el-button
                  size="small"
                  link
                  :class="row.myReminderOn ? 'reminder-on' : 'reminder-off'"
                  @click="openListReminderDialog(row)"
                >
                  提醒
                </el-button>
                <el-button v-if="canOperate(row)" size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
              </div>
            </div>
            <el-empty v-if="!loading && !tableData?.length" description="暂无墓碑" :image-size="80" />
          </div>
        </div>

        <div class="pagination-wrap">
          <el-pagination
            :small="isMobile"
            v-model:current-page="query.pageIndex"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadData"
            @size-change="loadData"
          />
        </div>
      </el-card>
    </div>

    <el-drawer v-model="treeDrawerVisible" title="选择家族" direction="ltr" size="280px">
      <el-tree
        v-loading="treeLoading"
        :data="familyTreeData"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        highlight-current
        default-expand-all
        @node-click="(node) => { handleTreeNodeClick(node); treeDrawerVisible = false }"
      />
    </el-drawer>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑墓碑' : '新增墓碑'" :width="isMobile ? '95%' : '720px'" destroy-on-close class="form-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <el-form-item label="逝者姓名" prop="name">
              <el-input v-model="form.name" maxlength="64" show-word-limit placeholder="最多64字" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <el-form-item label="所属家族">
              <el-tree-select
                v-model="form.familyId"
                :data="tombFamilyOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                check-strictly
                clearable
                placeholder="选择所属家族（可选）"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <el-form-item label="出生日期" prop="birthdaySolar">
              <el-date-picker
                v-model="form.birthdaySolar"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择年月日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <el-form-item label="逝世日期" prop="deathdaySolar">
              <el-date-picker
                v-model="form.deathdaySolar"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择年月日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="日期历法">
          <div class="tomb-date-cal-row">
            <el-radio-group v-model="form.dateCal" size="small">
              <el-radio-button value="solar">公历</el-radio-button>
              <el-radio-button value="lunar">农历</el-radio-button>
            </el-radio-group>
            <span class="field-hint tomb-cal-hint">仅标记上面日期按公历还是农历理解；控件相同。提醒时若为农历再按农历推算。</span>
          </div>
        </el-form-item>
        <el-form-item label="个人简介" prop="biography">
          <div class="quill-wrap">
            <QuillEditor v-model:content="form.biography" contentType="html" theme="snow" placeholder="墓主人个人简介（最多1000字）" />
          </div>
          <div class="field-hint">纯文字不超过1000字</div>
        </el-form-item>
        <el-form-item label="墓志铭" prop="epitaph">
          <el-input v-model="form.epitaph" maxlength="32" show-word-limit placeholder="如：音容宛在，永垂不朽（最多32字）" />
        </el-form-item>
        <el-form-item label="所处位置" prop="address">
          <el-input v-model="form.address" maxlength="200" show-word-limit placeholder="记录墓地所处位置（可选，最多200字）" />
        </el-form-item>
        <el-form-item label="开放互动">
          <el-switch
            v-model="form.visitorActionOpen"
            inline-prompt
            active-text="开放"
            inactive-text="仅家族"
          />
          <span class="story-tip" style="margin-left: 10px">
            关闭后：留言/献花等仅家族成员可操作（仍需登录）；开启后：所有人登录后可操作（支持匿名姓名）。
          </span>
        </el-form-item>
        <el-form-item label="生平事迹">
          <div class="story-tip">
            生平事迹已支持“多条事迹 + 富文本编辑”。请先保存墓碑，然后在列表中点击“事迹”进行维护。
          </div>
        </el-form-item>
        <el-form-item label="照片">
          <div class="photo-upload">
            <div v-if="form.photo" class="photo-preview">
              <el-image :src="form.photo" fit="cover" class="photo-img" />
              <div class="photo-actions">
                <el-icon @click="form.photo = ''"><Close /></el-icon>
              </div>
            </div>
            <div v-else class="photo-placeholder" @click="triggerUpload">
              <el-icon :size="28" color="#c0c4cc"><Plus /></el-icon>
              <span>上传照片</span>
            </div>
            <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleUpload" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTomb">保存</el-button>
      </template>
    </el-dialog>

    <!-- 事迹管理抽屉 -->
    <el-drawer v-model="storyVisible" :title="storyTomb.name + ' - 生平事迹'" :size="isMobile ? '100%' : '55%'">
      <div class="story-toolbar">
        <el-button type="primary" @click="openStoryEditor()"><el-icon><Plus /></el-icon>新增事迹</el-button>
      </div>

      <el-table :data="storyList" border stripe v-loading="storyLoading">
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openStoryEditor(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDeleteStory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="storyEditorVisible" :title="storyIsEdit ? '编辑事迹' : '新增事迹'" :width="isMobile ? '95%' : '800px'" destroy-on-close>
        <el-form :model="storyForm" ref="storyFormRef" label-width="80px">
          <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }, { max: 100, message: '标题不能超过100字', trigger: 'blur' }]">
            <el-input v-model="storyForm.title" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="storyForm.sort" :min="0" :max="9999" />
          </el-form-item>
          <el-form-item label="内容" prop="content" :rules="storyContentRules">
            <div class="quill-wrap">
              <QuillEditor v-model:content="storyForm.content" contentType="html" theme="snow" placeholder="事迹内容（纯文字最多2000字）" />
            </div>
            <div class="field-hint">纯文字不超过2000字</div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="storyEditorVisible = false">取消</el-button>
          <el-button type="primary" :loading="storySaving" @click="saveStory">保存</el-button>
        </template>
      </el-dialog>
    </el-drawer>

    <el-dialog
      v-model="listReminderDialogVisible"
      title="墓碑提醒"
      :width="isMobile ? '92%' : '560px'"
      destroy-on-close
      class="list-reminder-dialog"
      align-center
    >
      <el-form ref="listReminderFormRef" :model="listReminderForm" :rules="listReminderRules" v-bind="listReminderFormLabelAttrs">
        <el-form-item label="与墓主关系" prop="relationship">
          <el-select v-model="listReminderForm.relationship" placeholder="请选择" style="width: 100%">
            <el-option v-for="r in listReminderRelations" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item label="提醒日期" prop="eventTypes">
          <div class="list-reminder-event-checkboxes">
            <el-checkbox-group v-model="listReminderForm.eventTypes">
              <el-checkbox v-for="o in listReminderEventOptions" :key="o.value" :label="o.value">
                {{ o.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-form-item>
        <el-form-item v-if="listReminderForm.eventTypes.includes('CUSTOM')" label="自定义日期">
          <div class="custom-dates">
            <p v-if="listReminderTargetRow?.lunarFlag" class="reminder-custom-hint">
              本墓碑为农历日期；下方日期按农历月日理解，每年对应公历日会变动。
            </p>
            <p class="reminder-custom-dates-tip">
              日期会自动根据墓主信息公历/农历提示；备注为选填，可填写纪念日等。
            </p>
            <div v-for="i in [0, 1, 2]" :key="i" class="custom-date-row">
              <div class="custom-date-picker-wrap">
                <el-date-picker
                  v-model="listReminderCustomPickers[i]"
                  type="date"
                  value-format="YYYY-MM-DD"
                  class="custom-date-input"
                  clearable
                />
              </div>
              <el-input
                v-model="listReminderCustomRemarks[i]"
                maxlength="32"
                show-word-limit
                clearable
                class="reminder-custom-remark-input"
              />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="提前提醒">
          <el-checkbox v-model="listReminderForm.advance0">当天</el-checkbox>
          <el-checkbox v-model="listReminderForm.advance1">提前 1 天</el-checkbox>
          <el-checkbox v-model="listReminderForm.advance3">提前 3 天</el-checkbox>
          <div class="custom-advance-list">
            <div v-for="(n, i) in listReminderForm.customAdvanceDays" :key="i" class="custom-advance-row">
              <span>提前</span>
              <el-input-number v-model="listReminderForm.customAdvanceDays[i]" :min="1" :max="365" controls-position="right" />
              <span>天</span>
              <el-button type="danger" link @click="removeListReminderAdvance(i)">删除</el-button>
            </div>
            <el-button v-if="listReminderForm.customAdvanceDays.length < 8" type="primary" link @click="addListReminderAdvance">+ 自定义提前天数</el-button>
          </div>
        </el-form-item>
        <el-form-item label="提醒方式">
          <el-checkbox v-model="listReminderForm.channelSms" disabled>短信</el-checkbox>
          <el-checkbox v-model="listReminderForm.channelWechat" disabled>微信（即将支持公众号推送）</el-checkbox>
        </el-form-item>
        <el-form-item label="总开关">
          <el-switch v-model="listReminderForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="list-reminder-dialog-footer">
          <el-button @click="listReminderDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="listReminderSaving" @click="submitListReminder">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="qrDialogVisible" title="墓碑二维码" width="360px" destroy-on-close>
      <div class="invite-dialog-body" v-if="currentQr.tomb">
        <div class="invite-info">
          <div class="invite-title">{{ currentQr.tomb.name }}</div>
          <div class="invite-tip">扫码访问纪念页</div>
        </div>
        <div class="invite-qr-wrap">
          <img v-if="currentQr.qr" :src="currentQr.qr" class="invite-qr-img" />
          <span v-else class="text-muted">二维码生成中...</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="qrDialogVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!currentQr.qr" @click="downloadCurrentQr">下载二维码</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" :title="detailData.name + ' - 详细信息'" :size="isMobile ? '100%' : '45%'">
      <div class="detail-content">
        <div class="detail-photo-block">
          <div class="detail-photo" v-if="detailData.photo">
            <el-image :src="detailData.photo" fit="cover" style="width: 120px; height: 120px; border-radius: 8px" />
          </div>
          <div class="detail-photo" v-else>
            <div class="detail-photo-placeholder"><el-icon :size="48" color="#c0c4cc"><Picture /></el-icon></div>
          </div>
          <div class="detail-epitaph-under-photo" v-if="detailData.epitaph">{{ detailData.epitaph }}</div>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="逝者姓名">{{ detailData.name }}</el-descriptions-item>
          <el-descriptions-item label="所属家族">{{ detailData.familyName || '未关联' }}</el-descriptions-item>
          <el-descriptions-item label="所处位置" :span="2">{{ detailData.address || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="出生日期"><span v-html="formatTombBirth(detailData)"></span></el-descriptions-item>
          <el-descriptions-item label="逝世日期"><span v-html="formatTombDeath(detailData)"></span></el-descriptions-item>
          <el-descriptions-item label="访问量">{{ detailData.visitCount }}</el-descriptions-item>
          <el-descriptions-item label="留言数">{{ detailData.messageCount }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block" v-if="detailData.biography">
          <div class="detail-block-label">个人简介</div>
          <div class="detail-block-content detail-biography" v-html="detailData.biography"></div>
        </div>
        <div class="detail-block" v-else>
          <div class="detail-block-label">个人简介</div>
          <div class="detail-block-content detail-block-empty">暂无</div>
        </div>

        <div class="detail-stories" v-if="detailData.stories && detailData.stories.length">
          <h3 class="detail-stories-title">生平事迹</h3>
          <div class="detail-story-item" v-for="s in detailData.stories" :key="s.id">
            <div class="detail-story-title">{{ s.title }}</div>
            <div class="detail-story-content" v-html="s.content"></div>
          </div>
        </div>

        <el-divider />

        <el-tabs v-model="detailTab" class="detail-tabs">
          <el-tab-pane label="留言记录" name="messages">
            <el-table :data="messageTable" border stripe v-loading="messageLoading">
              <el-table-column prop="visitorName" label="访客" width="120" />
              <el-table-column label="内容" min-width="240" show-overflow-tooltip>
                <template #default="{ row }">
                  <span class="msg-content">{{ row.content }}</span>
                  <el-button size="small" type="primary" link @click="openMessageDialog(row)">查看</el-button>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-tag
                    :type="row.status === '通过' ? 'success' : row.status === '拒绝' ? 'danger' : 'warning'"
                  >
                    {{ row.status || '待审核' }}
                  </el-tag>
                  <el-tooltip v-if="row.status === '拒绝' && row.rejectReason" :content="row.rejectReason" placement="top">
                    <el-button size="small" type="danger" link>原因</el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="时间" width="170" />
            </el-table>
            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="messageQuery.pageIndex"
                v-model:page-size="messageQuery.pageSize"
                :total="messageTotal"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="loadMessages"
                @size-change="loadMessages"
              />
            </div>
          </el-tab-pane>

          <el-tab-pane label="献花/打卡记录" name="checkins">
            <el-table :data="checkinTable" border stripe v-loading="checkinLoading">
              <el-table-column prop="visitorName" label="访客" width="120" />
              <el-table-column prop="type" label="类型" width="120" />
              <el-table-column prop="ip" label="IP" width="120" />
              <el-table-column prop="location" label="位置" />
              <el-table-column prop="createTime" label="时间" width="170" />
            </el-table>
            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="checkinQuery.pageIndex"
                v-model:page-size="checkinQuery.pageSize"
                :total="checkinTotal"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="loadCheckins"
                @size-change="loadCheckins"
              />
            </div>
          </el-tab-pane>
        </el-tabs>

        <el-dialog v-model="messageDialogVisible" title="留言详情" width="560px" destroy-on-close>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="访客">{{ currentMessage.visitorName || '匿名' }}</el-descriptions-item>
            <el-descriptions-item label="时间">{{ currentMessage.createTime }}</el-descriptions-item>
            <el-descriptions-item label="状态" :span="2">
              <el-tag :type="currentMessage.status === '通过' ? 'success' : currentMessage.status === '拒绝' ? 'danger' : 'warning'">
                {{ currentMessage.status || '待审核' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMessage.status === '拒绝' && currentMessage.rejectReason" label="拒绝原因" :span="2">
              {{ currentMessage.rejectReason }}
            </el-descriptions-item>
            <el-descriptions-item label="内容" :span="2">
              <div class="msg-detail">{{ currentMessage.content }}</div>
            </el-descriptions-item>
          </el-descriptions>
          <template #footer>
            <el-button type="primary" @click="messageDialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Plus, Picture, Close, List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {
  getTombPageList,
  getTombDetail,
  addTomb,
  updateTomb,
  deleteTomb,
  getTombStoryList,
  addTombStory,
  updateTombStory,
  deleteTombStory,
  getMessagePageList,
  getCheckinPageList,
  getTombReminder,
  saveTombReminder
} from '@/api/tomb'
import { getFamilyTree } from '@/api/family'
import { uploadFile } from '@/api/user'
import { formatTombBirth, formatTombDeath } from '@/utils/tombCalendar'

const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const fileInput = ref()
const total = ref(0)
const detailData = ref({})
const familyTreeOptions = ref([])
const treeLoading = ref(false)
const isSuperAdmin = ref(false)
const familyTreeData = ref([])
const selectedFamilyId = ref(null)
const selectedFamilyName = ref('')
const treeDrawerVisible = ref(false)
const isMobile = ref(window.innerWidth < 768)
function checkMobile() { isMobile.value = window.innerWidth < 768 }

const query = reactive({ keyword: '', familyId: null, pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const form = reactive({
  id: null,
  name: '',
  birthdaySolar: '',
  deathdaySolar: '',
  dateCal: 'solar',
  familyId: null,
  photo: '',
  biography: '',
  story: '',
  epitaph: '',
  address: '',
  visitorActionOpen: true
})

// 剥离HTML获取纯文字长度
function getTextLength(html) {
  if (!html) return 0
  const div = document.createElement('div')
  div.innerHTML = html
  return (div.textContent || div.innerText || '').trim().length
}

const rules = {
  name: [
    { required: true, message: '请输入逝者姓名', trigger: 'blur' },
    { max: 64, message: '逝者姓名不能超过64字', trigger: 'blur' }
  ],
  epitaph: [{ max: 32, message: '墓志铭不能超过32字', trigger: 'blur' }],
  address: [{ max: 200, message: '所处位置不能超过200字', trigger: 'blur' }],
  biography: [{
    validator: (_, val, cb) => {
      if (getTextLength(val) > 1000) cb(new Error('个人简介纯文字不能超过1000字'))
      else cb()
    },
    trigger: 'blur'
  }],
  birthdaySolar: [
    {
      validator: (_, __, cb) => {
        if (!form.birthdaySolar) cb(new Error('请选择出生日期'))
        else cb()
      },
      trigger: 'change'
    }
  ],
  deathdaySolar: [
    {
      validator: (_, __, cb) => {
        if (!form.deathdaySolar) cb(new Error('请选择逝世日期'))
        else cb()
      },
      trigger: 'change'
    }
  ]
}

const storyContentRules = [
  { required: true, message: '请输入内容', trigger: 'blur' },
  {
    validator: (_, val, cb) => {
      if (getTextLength(val) > 2000) cb(new Error('事迹内容纯文字不能超过2000字'))
      else cb()
    },
    trigger: 'blur'
  }
]

async function loadData() {
  loading.value = true
  try {
    query.familyId = selectedFamilyId.value
    const res = await getTombPageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

// 所属家族：可选择任意节点
const tombFamilyOptions = computed(() => familyTreeOptions.value || [])

// 所有登录用户均可新增墓碑（可不选家族，添加个人墓碑）
const canAddTomb = computed(() => true)

async function loadFamilyTree() {
  const u = JSON.parse(localStorage.getItem('currentUser') || '{}')
  isSuperAdmin.value = u.role === 'admin'
  treeLoading.value = true
  try {
    const tree = await getFamilyTree()
    familyTreeOptions.value = tree || []
    familyTreeData.value = [{ id: -1, name: '全部家族', children: familyTreeOptions.value }]
  } catch {
    familyTreeOptions.value = []
    familyTreeData.value = [{ id: -1, name: '全部家族', children: [] }]
  } finally {
    treeLoading.value = false
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  loadData()
  loadFamilyTree()
})
onUnmounted(() => { window.removeEventListener('resize', checkMobile) })

const currentFamilyTitle = computed(() => {
  if (selectedFamilyId.value === null) return '当前筛选：全部家族'
  return `当前筛选：${selectedFamilyName.value || '家族'}（含子家族）`
})

function handleTreeNodeClick(node) {
  if (Number(node.id) === -1) {
    selectedFamilyId.value = null
    selectedFamilyName.value = ''
  } else {
    selectedFamilyId.value = Number(node.id)
    selectedFamilyName.value = node.name || ''
  }
  query.pageIndex = 1
  loadData()
}

function triggerUpload() { fileInput.value?.click() }

async function handleUpload(e) {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过5MB')
    return
  }
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.warning('仅支持 JPG/PNG/GIF/WebP 格式')
    return
  }
  try {
    const url = await uploadFile(file)
    if (url) {
      form.photo = url
      ElMessage.success('上传成功')
    }
  } catch { ElMessage.error('上传失败') }
  finally { e.target.value = '' }
}

function syncFormCalendarFields(row) {
  form.dateCal = row?.lunarFlag ? 'lunar' : 'solar'
  form.birthdaySolar = row?.birthday ? String(row.birthday).slice(0, 10) : ''
  form.deathdaySolar = row?.deathday ? String(row.deathday).slice(0, 10) : ''
}

function buildTombSubmitPayload() {
  const base = {
    id: form.id,
    name: form.name,
    photo: form.photo,
    biography: form.biography,
    story: form.story,
    epitaph: form.epitaph,
    visitorActionOpen: form.visitorActionOpen,
    familyId: form.familyId,
    address: form.address
  }
  if (!form.birthdaySolar || !form.deathdaySolar) {
    ElMessage.warning('请完整填写出生、逝世日期')
    return null
  }
  const lunar = form.dateCal === 'lunar'
  base.birthday = form.birthdaySolar
  base.deathday = form.deathdaySolar
  base.lunarFlag = lunar
  return base
}

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { visitorActionOpen: true, ...row })
    if (form.visitorActionOpen === null || form.visitorActionOpen === undefined) form.visitorActionOpen = true
    syncFormCalendarFields(row)
  } else {
    Object.assign(form, {
      id: null,
      name: '',
      birthdaySolar: '',
      deathdaySolar: '',
      dateCal: 'solar',
      familyId: null,
      photo: '',
      biography: '',
      story: '',
      epitaph: '',
      address: '',
      visitorActionOpen: true
    })
  }
  loadFamilyTree()
  dialogVisible.value = true
}

async function saveTomb() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const payload = buildTombSubmitPayload()
    if (!payload) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateTomb(payload)
        ElMessage.success('修改成功')
      } else {
        await addTomb(payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch { /* 错误由拦截器处理 */ }
    finally { saving.value = false }
  })
}

/** 纪念页在新标签页打开，不离开当前列表 */
function goMemorialPage(row) {
  if (!row?.id) return
  const resolved = router.resolve({ path: `/memorial/${row.id}` })
  const url = resolved.href.startsWith('http') ? resolved.href : `${window.location.origin}${resolved.href}`
  window.open(url, '_blank', 'noopener,noreferrer')
}

function viewDetail(row) {
  detailVisible.value = true
  detailData.value = { ...row }
  getTombDetail(row.id)
    .then((res) => {
      if (res) detailData.value = res
      // 打开详情默认加载留言/打卡
      detailTab.value = 'messages'
      resetDetailLists(row.id)
    })
    .catch(() => {})
}

function resetDetailLists(tombId) {
  messageQuery.tombId = tombId
  messageQuery.pageIndex = 1
  checkinQuery.tombId = tombId
  checkinQuery.pageIndex = 1
  loadMessages()
  loadCheckins()
}

// =========================
// 详情：留言/打卡记录
// =========================

const detailTab = ref('messages')

const messageLoading = ref(false)
const messageTotal = ref(0)
const messageTable = ref([])
const messageQuery = reactive({ tombId: null, keyword: '', status: '', pageIndex: 1, pageSize: 10 })

const messageDialogVisible = ref(false)
const currentMessage = reactive({ id: null, visitorName: '', content: '', status: '', rejectReason: '', createTime: '' })

function openMessageDialog(row) {
  Object.assign(currentMessage, {
    id: row.id,
    visitorName: row.visitorName,
    content: row.content,
    status: row.status,
    rejectReason: row.rejectReason,
    createTime: row.createTime
  })
  messageDialogVisible.value = true
}

async function loadMessages() {
  if (!messageQuery.tombId) return
  messageLoading.value = true
  try {
    const res = await getMessagePageList(messageQuery)
    if (res) {
      messageTable.value = res.records || []
      messageTotal.value = res.total || 0
    }
  } catch { /* */ }
  finally { messageLoading.value = false }
}

const checkinLoading = ref(false)
const checkinTotal = ref(0)
const checkinTable = ref([])
const checkinQuery = reactive({ tombId: null, keyword: '', type: '', pageIndex: 1, pageSize: 10 })

async function loadCheckins() {
  if (!checkinQuery.tombId) return
  checkinLoading.value = true
  try {
    const res = await getCheckinPageList(checkinQuery)
    if (res) {
      checkinTable.value = res.records || []
      checkinTotal.value = res.total || 0
    }
  } catch { /* */ }
  finally { checkinLoading.value = false }
}

// =========================
// 列表：二维码弹窗（同家族邀请码风格）
// =========================

const qrDialogVisible = ref(false)
const currentQr = reactive({ tomb: null, qr: '' })

async function openQrDialog(row) {
  currentQr.tomb = row
  currentQr.qr = ''
  qrDialogVisible.value = true
  try {
    const code = row.qrCodeKey || row.id
    const url = `${window.location.origin}/memorial/${code}`
    currentQr.qr = await QRCode.toDataURL(url, { width: 320, margin: 1 })
  } catch {
    ElMessage.error('生成二维码失败')
  }
}

function downloadCurrentQr() {
  if (!currentQr.qr || !currentQr.tomb) return
  const link = document.createElement('a')
  link.download = `墓碑二维码_${currentQr.tomb.name}.png`
  link.href = currentQr.qr
  link.click()
}

function canOperate(row) {
  return row && ['admin', 'chief', 'member'].includes(row.myRole)
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除墓碑 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTomb(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
}

// =========================
// 事迹管理
// =========================

const storyVisible = ref(false)
const storyLoading = ref(false)
const storySaving = ref(false)
const storyList = ref([])
const storyTomb = reactive({ id: null, name: '' })
const storyEditorVisible = ref(false)
const storyIsEdit = ref(false)
const storyFormRef = ref()
const storyForm = reactive({ id: null, tombId: null, title: '', content: '', sort: 0 })

async function openStoryDrawer(row) {
  storyTomb.id = row.id
  storyTomb.name = row.name
  storyVisible.value = true
  await loadStoryList()
}

async function loadStoryList() {
  if (!storyTomb.id) return
  storyLoading.value = true
  try {
    const res = await getTombStoryList(storyTomb.id)
    storyList.value = res || []
  } catch { /* */ }
  finally { storyLoading.value = false }
}

function openStoryEditor(row) {
  storyIsEdit.value = !!row
  if (row) Object.assign(storyForm, { id: row.id, tombId: row.tombId, title: row.title, content: row.content, sort: row.sort ?? 0 })
  else Object.assign(storyForm, { id: null, tombId: storyTomb.id, title: '', content: '', sort: 0 })
  storyEditorVisible.value = true
}

function saveStory() {
  storyFormRef.value.validate(async (valid) => {
    if (!valid) return
    storySaving.value = true
    try {
      if (storyIsEdit.value) await updateTombStory(storyForm)
      else await addTombStory(storyForm)
      ElMessage.success('保存成功')
      storyEditorVisible.value = false
      loadStoryList()
    } catch { /* */ }
    finally { storySaving.value = false }
  })
}

function handleDeleteStory(row) {
  ElMessageBox.confirm(`确定删除事迹 "${row.title}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTombStory(row.id)
      ElMessage.success('删除成功')
      loadStoryList()
    })
}

// =========================
// 列表：个人提醒（与纪念页同一套接口）
// =========================

const listReminderRelations = ['父亲', '母亲', '儿子', '女儿', '兄弟', '姐妹', '爷爷', '奶奶', '外公', '外婆', '叔伯', '姑姨', '其他']
const listReminderEventOptions = [
  { value: 'DEATH_ANNIVERSARY', label: '忌日（按逝世日期）' },
  { value: 'QINGMING', label: '清明节（公历约4月5日）' },
  { value: 'CHONGYANG', label: '重阳节（农历九月初九）' },
  { value: 'BIRTHDAY', label: '生辰（按出生日期）' },
  { value: 'CUSTOM', label: '自定义（最多3个日期）' }
]

const listReminderDialogVisible = ref(false)
const listReminderSaving = ref(false)
const listReminderFormRef = ref()
const listReminderTargetRow = ref(null)
const listReminderSaved = ref(null)
const listReminderCustomPickers = ref([null, null, null])
const listReminderCustomRemarks = ref(['', '', ''])

const listReminderForm = reactive({
  relationship: '',
  eventTypes: [],
  advance0: true,
  advance1: false,
  advance3: false,
  customAdvanceDays: [],
  channelSms: true,
  channelWechat: false,
  enabled: true
})

const listReminderRules = {
  relationship: [{ required: true, message: '请选择与墓主关系', trigger: 'change' }],
  eventTypes: [{ type: 'array', required: true, min: 1, message: '请至少选择一种日期类型', trigger: 'change' }]
}

/** 窄屏表单项标签置顶，避免挤压 */
const listReminderFormLabelAttrs = computed(() =>
  isMobile.value ? { labelPosition: 'top' } : { labelPosition: 'right', labelWidth: '110px' }
)

function apiStrToListPicker(str) {
  if (!str || !String(str).trim()) return null
  const t = String(str).trim()
  if (/^\d{4}-\d{2}-\d{2}$/.test(t)) return t
  if (/^\d{2}-\d{2}$/.test(t)) {
    const y = new Date().getFullYear()
    const [m, d] = t.split('-')
    return `${y}-${m}-${d}`
  }
  return null
}

function fillListReminderForm() {
  const r = listReminderSaved.value
  if (!r) {
    listReminderForm.relationship = ''
    listReminderForm.eventTypes = []
    listReminderCustomPickers.value = [null, null, null]
    listReminderCustomRemarks.value = ['', '', '']
    listReminderForm.advance0 = true
    listReminderForm.advance1 = false
    listReminderForm.advance3 = false
    listReminderForm.customAdvanceDays = []
    listReminderForm.enabled = true
    listReminderForm.channelSms = true
    listReminderForm.channelWechat = false
    return
  }
  listReminderForm.relationship = r.relationship || ''
  listReminderForm.eventTypes = [...(r.eventTypes || [])]
  const cds = r.customDates || []
  listReminderCustomPickers.value = [apiStrToListPicker(cds[0]), apiStrToListPicker(cds[1]), apiStrToListPicker(cds[2])]
  const rem = r.customDateRemarks || []
  listReminderCustomRemarks.value = [rem[0] || '', rem[1] || '', rem[2] || '']
  const offs = r.advanceOffsets || []
  listReminderForm.advance0 = offs.includes(0)
  listReminderForm.advance1 = offs.includes(1)
  listReminderForm.advance3 = offs.includes(3)
  listReminderForm.customAdvanceDays = offs.filter((x) => x !== 0 && x !== 1 && x !== 3)
  listReminderForm.enabled = r.enabled !== false
  listReminderForm.channelSms = r.channelSms !== false
  listReminderForm.channelWechat = !!r.channelWechat
}

async function openListReminderDialog(row) {
  listReminderTargetRow.value = row
  if (!row?.id) return
  try {
    const r = await getTombReminder(row.id)
    listReminderSaved.value = r || null
  } catch {
    listReminderSaved.value = null
  }
  fillListReminderForm()
  listReminderDialogVisible.value = true
}

function addListReminderAdvance() {
  listReminderForm.customAdvanceDays.push(7)
}

function removeListReminderAdvance(i) {
  listReminderForm.customAdvanceDays.splice(i, 1)
}

function buildListReminderCustomDatesAndRemarks() {
  const dates = []
  const remarks = []
  for (let i = 0; i < 3; i++) {
    const d = listReminderCustomPickers.value[i]
    if (d && String(d).trim()) {
      dates.push(String(d).trim())
      remarks.push((listReminderCustomRemarks.value[i] || '').trim())
    }
  }
  return { dates, remarks }
}

async function submitListReminder() {
  const row = listReminderTargetRow.value
  if (!row?.id) return
  try {
    await listReminderFormRef.value?.validate()
  } catch {
    return
  }
  if (listReminderForm.eventTypes.includes('CUSTOM')) {
    const { dates } = buildListReminderCustomDatesAndRemarks()
    if (dates.length === 0) {
      ElMessage.warning('请选择自定义日期或取消勾选「自定义」')
      return
    }
  }
  const base = []
  if (listReminderForm.advance0) base.push(0)
  if (listReminderForm.advance1) base.push(1)
  if (listReminderForm.advance3) base.push(3)
  const customAdv = listReminderForm.customAdvanceDays.filter((n) => n != null && n >= 1 && n <= 365)
  if (base.length === 0 && customAdv.length === 0) {
    ElMessage.warning('请至少选择一种提前提醒方式')
    return
  }
  let customDates
  let customDateRemarksPayload
  if (listReminderForm.eventTypes.includes('CUSTOM')) {
    const { dates, remarks } = buildListReminderCustomDatesAndRemarks()
    customDates = dates
    customDateRemarksPayload = remarks
  }
  listReminderSaving.value = true
  try {
    await saveTombReminder({
      id: listReminderSaved.value?.id,
      tombId: row.id,
      relationship: listReminderForm.relationship,
      enabled: listReminderForm.enabled,
      channelSms: listReminderForm.channelSms,
      channelWechat: listReminderForm.channelWechat,
      eventTypes: listReminderForm.eventTypes,
      customDates,
      customDateRemarks: customDateRemarksPayload,
      advanceOffsets: base,
      customAdvanceDays: customAdv
    })
    ElMessage.success('提醒已保存')
    listReminderDialogVisible.value = false
    loadData()
  } catch {
    /* 拦截器 */
  } finally {
    listReminderSaving.value = false
  }
}
</script>

<style scoped>
.page-wrap { display: flex; gap: 14px; }
.left-card { width: 280px; flex: 0 0 280px; }
.right-card { flex: 1; }
.left-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.left-title { font-size: 14px; font-weight: 600; color: #303133; }

.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; flex-wrap: wrap; gap: 10px; }
.toolbar-mobile { flex-direction: column; align-items: stretch; }
.toolbar-mobile .toolbar-row { display: flex; gap: 10px; align-items: center; }
.toolbar-mobile .toolbar-row-1 { justify-content: space-between; }
/* 搜索行：输入框与按钮同一行、垂直居中（双类名提高优先级，避免被下方通用规则覆盖） */
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.toolbar-left.toolbar-left--search {
  flex-wrap: nowrap;
  flex: 1;
  min-width: 0;
}
.toolbar-left.toolbar-left--search .search-input {
  flex: 1;
  min-width: 120px;
  max-width: 400px;
}
.toolbar-left.toolbar-left--search .search-btn {
  flex-shrink: 0;
}
.toolbar-mobile .toolbar-row-search {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10px;
}
.toolbar-mobile .toolbar-row-search .search-input {
  flex: 1;
  min-width: 0;
  max-width: none;
}
.toolbar-mobile .toolbar-row-search .search-btn {
  flex-shrink: 0;
}
.search-input { min-width: 140px; max-width: 220px; }
.current-family { margin-bottom: 10px; font-size: 13px; color: #909399; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.change-family-link { flex-shrink: 0; }
.tree-toggle-btn { flex-shrink: 0; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.text-muted { color: #c0c4cc; font-size: 13px; }

/* 墓碑列表：桌面表格 / 手机卡片切换 */
.tomb-desktop { display: block; }
.tomb-mobile { display: none; }
.tomb-card-list { display: flex; flex-direction: column; gap: 12px; }
.tomb-card {
  padding: 14px;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  background: #fafafa;
}
.tomb-card__main { display: flex; gap: 14px; margin-bottom: 12px; }
.tomb-card__photo { flex-shrink: 0; border-radius: 8px; }
.tomb-card__info { flex: 1; min-width: 0; }
.tomb-card__name { font-weight: 600; font-size: 16px; color: #303133; margin-bottom: 4px; }
.tomb-card__meta { font-size: 13px; color: #606266; margin-bottom: 6px; }
.tomb-card__family { margin-bottom: 4px; }
.tomb-card__stats { font-size: 12px; color: #909399; }
.tomb-card__actions { display: flex; flex-wrap: wrap; gap: 4px; padding-top: 10px; border-top: 1px solid #ebeef5; }

@media (max-width: 768px) {
  .page-wrap { flex-direction: column; }
  .left-card { display: none; }
  .right-card { flex: 1; min-width: 0; width: 100%; overflow-x: visible; }
  .tomb-desktop { display: none; }
  .tomb-mobile { display: block; }
  .search-input { flex: 1; min-width: 100px; max-width: none; }
  .pagination-wrap { overflow-x: auto; }
  .pagination-wrap :deep(.el-pagination) { flex-wrap: wrap; }
}

.detail-content { padding: 0 10px; overflow-y: auto; max-height: calc(100vh - 60px); }
.detail-photo-block { margin-bottom: 16px; }
.detail-photo { text-align: center; margin-bottom: 8px; }
.detail-photo-placeholder { width: 120px; height: 120px; margin: 0 auto; border: 1px dashed #d9d9d9; border-radius: 8px; display: flex; align-items: center; justify-content: center; background: #fafafa; }
.detail-epitaph-under-photo {
  text-align: center;
  color: #606266;
  font-size: 14px;
  font-weight: 600;
  font-style: normal;
  line-height: 1.6;
  word-break: break-word;
  max-width: 180px;
  margin: 0 auto;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
}
.detail-block { margin-top: 16px; }
.detail-block-label { font-weight: 600; font-size: 14px; color: #303133; margin-bottom: 8px; padding: 8px 12px; background: #f5f7fa; border-radius: 6px; }
.detail-block-content { padding: 10px 12px; border: 1px solid #ebeef5; border-radius: 6px; background: #fafafa; color: #606266; line-height: 1.8; }
.detail-block-content.detail-biography { max-height: 200px; overflow-y: auto; }
.detail-block-empty { color: #c0c4cc; }
.photo-upload { display: flex; align-items: flex-start; }
.photo-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}
.photo-img { width: 100%; height: 100%; }
.photo-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  background: rgba(0,0,0,0.5);
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.photo-placeholder {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  gap: 6px;
  color: #c0c4cc;
  transition: border-color 0.3s;
}
.photo-placeholder:hover { border-color: #409eff; color: #409eff; }
.story-tip { font-size: 13px; color: #909399; line-height: 1.6; }
.field-hint { font-size: 12px; color: #909399; margin-top: 4px; }
.story-toolbar { margin-bottom: 12px; display: flex; justify-content: flex-end; }
.quill-wrap { width: 100%; }

.detail-tabs { margin-top: 10px; }
.detail-stories { margin-top: 16px; }
.detail-stories-title { margin: 0 0 10px; font-size: 16px; color: #303133; }
.detail-story-item { padding: 10px 12px; border: 1px solid #ebeef5; border-radius: 8px; margin-bottom: 12px; }
.detail-story-title { font-weight: 600; margin-bottom: 8px; color: #303133; }
.detail-story-content :deep(p) { margin: 6px 0; line-height: 1.8; color: #606266; }
.detail-story-content :deep(img) { max-width: 100%; border-radius: 6px; }
.detail-biography :deep(p) { margin: 6px 0; line-height: 1.8; color: #606266; }
.detail-biography :deep(img) { max-width: 100%; border-radius: 6px; }
.msg-content { margin-right: 8px; }
.msg-detail { white-space: pre-wrap; line-height: 1.8; color: #606266; }

/* 二维码列：单元格内水平+垂直居中 */
:deep(.el-table__body-wrapper .el-table__body td.col-qr-preview) {
  vertical-align: middle;
}
:deep(.el-table__body-wrapper .el-table__body td.col-qr-preview .cell) {
  display: flex !important;
  justify-content: center;
  align-items: center;
  min-height: 56px;
  padding-top: 10px;
  padding-bottom: 10px;
}
.qr-mem-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  line-height: 1.3;
  text-align: center;
}
.reminder-on { color: #67c23a !important; font-weight: 600; }
.reminder-off { color: #909399 !important; }
.tomb-card__qr-mem {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 12px;
  padding: 10px 0 8px;
  border-top: 1px solid #ebeef5;
  margin-bottom: 4px;
}
.small-hint { font-size: 12px; color: #909399; margin-top: 6px; }
.list-reminder-dialog :deep(.el-checkbox) { margin-right: 12px; margin-bottom: 6px; }
.list-reminder-dialog-footer { display: flex; justify-content: flex-end; flex-wrap: wrap; gap: 10px; }
.list-reminder-dialog .custom-dates { display: flex; flex-direction: column; gap: 8px; width: 100%; max-width: 340px; }
.list-reminder-dialog .reminder-custom-hint { margin: 0 0 6px; font-size: 12px; color: #909399; line-height: 1.45; }
.list-reminder-dialog .reminder-custom-dates-tip {
  margin: 0 0 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.list-reminder-dialog .custom-date-row {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
}
.list-reminder-dialog .custom-date-picker-wrap {
  flex: 0 0 112px;
  width: 112px;
  max-width: 112px;
  min-width: 0;
}
.list-reminder-dialog .custom-date-picker-wrap :deep(.el-date-editor) {
  width: 100% !important;
  max-width: 100% !important;
  box-sizing: border-box;
}
.list-reminder-dialog .custom-date-picker-wrap :deep(.el-input__wrapper) {
  padding-left: 6px;
  padding-right: 4px;
}
.list-reminder-dialog .custom-date-picker-wrap :deep(.el-input__inner) {
  font-size: 12px;
}
.list-reminder-dialog .reminder-custom-remark-input {
  flex: 1;
  min-width: 0;
  max-width: 213px;
}
.list-reminder-dialog .reminder-custom-remark-input :deep(.el-input__wrapper) {
  box-sizing: border-box;
}
.list-reminder-dialog .custom-advance-list { margin-top: 8px; width: 100%; }
.list-reminder-dialog .custom-advance-row { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; flex-wrap: wrap; }

/* 墓碑列表 — 提醒弹窗：手机适配（与纪念页 reminder-dialog 一致） */
@media (max-width: 767px) {
  .list-reminder-dialog.el-dialog {
    max-width: 100%;
    margin: 12px auto !important;
    max-height: calc(100vh - 24px);
    display: flex;
    flex-direction: column;
  }
  .list-reminder-dialog :deep(.el-dialog__header) {
    padding: 14px 16px 8px;
    flex-shrink: 0;
  }
  .list-reminder-dialog :deep(.el-dialog__body) {
    padding: 0 16px;
    overflow-y: auto;
    flex: 1;
    min-height: 0;
    -webkit-overflow-scrolling: touch;
  }
  .list-reminder-dialog :deep(.el-dialog__footer) {
    padding: 10px 16px 16px;
    flex-shrink: 0;
  }
  .list-reminder-dialog :deep(.el-form-item) {
    margin-bottom: 16px;
  }
  .list-reminder-dialog :deep(.el-form-item__label) {
    line-height: 1.4;
  }
  .list-reminder-event-checkboxes :deep(.el-checkbox-group) {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    width: 100%;
  }
  .list-reminder-event-checkboxes :deep(.el-checkbox) {
    margin-right: 0;
    margin-bottom: 4px;
    white-space: normal;
    align-items: flex-start;
    height: auto;
    line-height: 1.45;
  }
  .list-reminder-dialog .custom-dates {
    max-width: none;
  }
  .list-reminder-dialog .custom-date-row {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  .list-reminder-dialog .custom-date-picker-wrap {
    width: 100% !important;
    max-width: 100% !important;
    flex: 1 1 auto;
  }
  .list-reminder-dialog .reminder-custom-remark-input {
    max-width: 100% !important;
    width: 100%;
  }
  .list-reminder-dialog-footer {
    flex-direction: column-reverse;
    width: 100%;
    gap: 8px;
  }
  .list-reminder-dialog-footer .el-button {
    width: 100%;
    margin: 0;
  }
}
.tomb-date-cal-row { display: flex; flex-wrap: wrap; align-items: center; gap: 6px; }
.tomb-cal-hint { margin-left: 4px; font-size: 12px; color: #909399; }
</style>
